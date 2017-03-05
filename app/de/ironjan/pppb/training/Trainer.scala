package de.ironjan.pppb.training

import com.google.inject.Inject
import de.ironjan.pppb.core.model.ParkingDataSet
import de.ironjan.pppb.core.model.DateTimeHelper._
import de.ironjan.pppb.core.repository.ParkingDataRepository
import org.joda.time.DateTime
import play.api.Logger
import smile.regression.{GradientTreeBoost, RandomForest, Regression, RegressionTree}

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by Jan Lippert on 19.02.2017.
  */
class Trainer @Inject()(parkingDataRepository: ParkingDataRepository) {

  def findBestModel = {
    getTrainedModels(trainingMethod = smallTraining)
      .map(_.minBy(_._1))
  }

  def doSomethingGreat = {
    getTrainedModels(trainingMethod = extensiveTraining)
      .map(_.minBy(_._1))
  }

  def findImportances = {
    val x = getTrainedModels(trainingMethod = extensiveTraining)
      .map{stream =>
        stream.map{
        case (_, rt: RegressionTree) => rt.importance()
        case (_, rf : RandomForest) => rf.importance()
        case (_, gtb: GradientTreeBoost) => gtb.importance()
      }
          .transpose.map(x => x.count(d => true))
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
    val boundary = ds.length * 9 / 10
    val splitSet = (ds.slice(0, boundary), ds.slice(boundary, ds.length - 1))

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

  private def smallTraining(x: Array[Array[Double]], y: Array[Double], testSet: Seq[ParkingDataSet]) =
    Stream(
      evaluate(smile.regression.cart(x, y, 100), testSet),
      evaluate(smile.regression.randomForest(x, y), testSet),
      evaluate(smile.regression.gbm(x, y, shrinkage = 1), testSet),
      evaluate(smile.regression.gbm(x, y, shrinkage = 0.1), testSet),
      evaluate(smile.regression.gbm(x, y, shrinkage = 0.01), testSet)
    )


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

