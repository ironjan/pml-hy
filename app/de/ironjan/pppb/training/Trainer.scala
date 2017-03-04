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
  def timeModelEvaluation = {
    Logger.debug(s"Started training.")

    val ds = Await.result(parkingDataRepository.getAll, Duration.Inf)

    val ts = DateTime.now()

    findBestModel(ds)

    val totalTrainingTime = DateTime.now().getMillis - ts.getMillis
    Logger.debug(s"Total training time for all subsets: ${totalTrainingTime}ms.")
  }

  def findBestModel(ds: Seq[ParkingDataSet]): (Double, Regression[Array[Double]] ) = {
    // Append checking set & predict it
    val boundary = ds.length * 9 / 10
    val splitSet = (ds.slice(0, boundary), ds.slice(boundary, ds.length - 1))

    // TODO just using get on option
    val best = evaluateModels(splitSet, splitSet._1.head.capacity.get)
      .sortBy(_._1)
      .head
    Logger.info(s"Found best model: (${best._1}, ${toPrintable(best._2)}")
    best
  }

  private def evaluateModels(ds: (Seq[ParkingDataSet], Seq[ParkingDataSet]), capacity: Int) = {
    val trainingSet = ds._1
    val testSet = ds._2
    Logger.debug(s"Training subset of length ${trainingSet.length} and test set of length ${testSet.length}.")

    val unzipped = unzipSet(trainingSet)

    // TODO better way for double .toArray?
    val x = unzipped._1.toArray
    val y = unzipped._2.toArray


    Logger.debug(s"Prepared training data.")

    Stream(
      evaluate(smile.regression.cart(x, y, 100), testSet),
      evaluate(smile.regression.randomForest(x, y), testSet),
      evaluate(smile.regression.gbm(x, y, shrinkage = 1), testSet),
      evaluate(smile.regression.gbm(x, y, shrinkage = 0.1), testSet),
      evaluate(smile.regression.gbm(x, y, shrinkage = 0.01), testSet),
      evaluate(smile.regression.gbm(x, y, shrinkage = 0.001), testSet))
  }

  private def evaluate(regression: Regression[Array[Double]], T: Seq[ParkingDataSet]) = {
    val xStars = unzipSet(T)._1
    val yStars = unzipSet(T)._2

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
        s"GradientTreeBoost: importance = [$importance]"
      }
      case r => r.getClass.getName
    }
  }
}

