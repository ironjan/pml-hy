package de.ironjan.pppb.training

import com.google.inject.Inject
import de.ironjan.pppb.core.MeanStd
import de.ironjan.pppb.core.model.ParkingDataSet
import de.ironjan.pppb.core.model.DateTimeHelper._
import de.ironjan.pppb.core.repository.ParkingDataRepository
import org.joda.time.DateTime
import play.api.Logger
import smile.regression.{GradientTreeBoost, RandomForest, Regression, RegressionTree}

import scala.annotation.tailrec
import util.Random.shuffle
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by Jan Lippert on 19.02.2017.
  */
class Trainer @Inject()(parkingDataRepository: ParkingDataRepository) {


  def findBestModel = {
    getTrainedModels(trainingMethod = smallTraining)
      .map(models => findMin(models, models.head))
  }

  def doSomethingGreat = {
    getTrainedModels(trainingMethod = extensiveTraining)
      .map(models => findMin(models, models.head))
  }

  @tailrec
  def findMin(models: Stream[(Double, Regression[Array[Double]])], best: (Double, Regression[Array[Double]]))
  : (Double, Regression[Array[Double]] =
    if (models.isEmpty) best else {
      val head = models.head
      val min = if(head._1 < best._1) head else best
      findMin(models.tail, min)
    }

  def findImportances = {
    val descr = Stream("hourOfDay", "minuteOfHour", "dayOfWeek", "dayOfMonth",
      "weekOfMonth", 
      "weekOfYear")
      

    val x = getTrainedModels(trainingMethod = extensiveTraining)
      .map{stream =>
        stream.map{
        case (_, rt: RegressionTree) => rt.importance()
        case (_, rf : RandomForest) => rf.importance()
        case (_, gtb: GradientTreeBoost) => gtb.importance()
      }
          .transpose
          .map(x => MeanStd.meanStd(x.toArray))
          .sortBy(_._1)
          .zip(descr)
      }

    x
  }

  private def getTrainedModels(trainingMethod: (Array[Array[Double]], Array[Double], Seq[ParkingDataSet]) => Stream[(Double, Regression[Array[Double]])]) = {
    parkingDataRepository.getAll
      .map(prepareTrainingData)
      .map {
        case (x: Array[Array[Double]], y: Array[Double], testSet: Seq[ParkingDataSet]) => trainingMethod(x, y, testSet)
      }
  }

  private def prepareTrainingData(ds: Seq[ParkingDataSet]) = {
    val boundary = ds.length * 8 / 10
    val shuffled = shuffle(ds)
    val splitSet = (shuffled.slice(0, boundary), shuffled.slice(boundary, shuffled.length - 1))

    val trainingSet = splitSet._1
    val testSet = splitSet._2

    Logger.debug(s"Training subset of length ${trainingSet.length} and test set of length ${testSet.length}.")

    val unzipped = unzipSet(trainingSet)

    // TODO better way for double .toArray?
    val x = unzipped._1.toArray
    val y = unzipped._2.toArray

    (x, y, testSet)
  }

  private def extensiveTraining(x: Array[Array[Double]], y: Array[Double], testSet: Seq[ParkingDataSet]) =
  Stream(
    evaluate(smile.regression.cart(x, y, 100), testSet),
    evaluate(smile.regression.randomForest(x, y), testSet),
    evaluate(smile.regression.gbm(x, y, shrinkage = 1), testSet),
    evaluate(smile.regression.gbm(x, y, shrinkage = 0.1), testSet),
    evaluate(smile.regression.gbm(x, y, shrinkage = 0.01), testSet),
    evaluate(smile.regression.gbm(x, y, shrinkage = 0.01, ntrees = 1), testSet),
    evaluate(smile.regression.gbm(x, y, shrinkage = 0.01, ntrees = 10), testSet),
    evaluate(smile.regression.gbm(x, y, shrinkage = 0.01, ntrees = 50), testSet),
    evaluate(smile.regression.gbm(x, y, shrinkage = 0.01, ntrees = 100), testSet),
    evaluate(smile.regression.gbm(x, y, shrinkage = 0.01, ntrees = 500), testSet),
    evaluate(smile.regression.gbm(x, y, shrinkage = 0.01, maxNodes = 6), testSet),
    evaluate(smile.regression.gbm(x, y, shrinkage = 0.01, maxNodes = 36), testSet)
  )

  val gmbStepWidth = 0.02
  val gbmSteps: Int = 10 //(1.0/2/gmbStepWidth - 1).toInt

  private def smallTraining(x: Array[Array[Double]], y: Array[Double], testSet: Seq[ParkingDataSet]) =
    Stream.concat(
      evaluate(smile.regression.cart(x, y, 100), testSet),
      evaluate(smile.regression.randomForest(x, y), testSet),
      evaluate(smile.regression.gbm(x, y, shrinkage = 1), testSet),
      evaluate(smile.regression.gbm(x, y, shrinkage = 0.01, maxNodes = 4, ntrees = 500), testSet),
      Stream.tabulate(gbmSteps) { i => (i + 1) * gmbStepWidth }
        .map(s => evaluate(smile.regression.gbm(x, y, shrinkage = s), testSet)))


  private def evaluate(regression: Regression[Array[Double]], T: Seq[ParkingDataSet]) = {
    val (xStars: Seq[Array[Double]], yStars: Seq[Double]) = unzipSet(T)

    val aes = xStars.map(regression.predict)
      .zip(yStars)
      .map(p => Math.abs(p._1 - p._2))

    val mae = aes.sum / aes.length
    Logger.debug(s"${toPrintable(regression)} had a mean average error of $mae.")
    (mae, regression)
  }

  private def unzipSet(trainingSet: Seq[ParkingDataSet]) = {
    trainingSet.filter(_.hasUsefulData)
      .map(_.toMlTrainingTuple)
      .unzip
  }

  def toPrintable(regression: Regression[Array[Double]]): String = {
    regression match {
      case rt: RegressionTree => {
        val importance = rt.importance().mkString(", ")
        s"RegressionTree: maxDepth = ${rt.maxDepth()}, importance = [$importance]"
      }
      case rf : RandomForest => {
        val importance = rf.importance().mkString(", ")
        s"RandomForest: importance = [$importance]"
      }
      case gtb: GradientTreeBoost => {
        gtb.getSamplingRate
        val importance = gtb.importance().mkString(", ")
        s"GradientTreeBoost: importance = [$importance], nTrees = ${gtb.getTrees.length}, maxNodes = ${gtb.getmaxNodes()}"
      }
      case r => r.getClass.getName
    }
  }
}

