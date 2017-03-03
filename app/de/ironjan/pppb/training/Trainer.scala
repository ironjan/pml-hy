package de.ironjan.pppb.training

import com.google.inject.Inject
import de.ironjan.pppb.core.model.ParkingDataSet
import de.ironjan.pppb.core.model.DateTimeHelper._
import de.ironjan.pppb.core.repository.ParkingDataRepository
import org.joda.time.DateTime
import play.api.Logger
import smile.regression.Regression

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
    evaluateModels(splitSet, splitSet._1.head.capacity.get)
      .sortBy(_._1)
      .head
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

    val gmbStepWidth = 0.1
    val gbmSteps = (1/gmbStepWidth - 1).toInt
    Seq.concat(
      Seq(evaluate(smile.regression.cart(x, y, 100), testSet),
      evaluate(smile.regression.randomForest(x, y), testSet)),
      Seq.tabulate(gbmSteps) { i => (i + 1) * gmbStepWidth }.map(s => evaluate(smile.regression.gbm(x, y, shrinkage = s), testSet)))
  }

  private def evaluate(regression: Regression[Array[Double]], T: Seq[ParkingDataSet]) = {
    val xStars = unzipSet(T)._1
    val yStars = unzipSet(T)._2

    val aes = xStars.map(regression.predict)
      .zip(yStars)
      .map(p => Math.abs(p._1 - p._2))

    val mae = aes.sum / aes.length
    Logger.debug(s"${regression.getClass.getName} had a mean average error of $mae.")
    (mae, regression)
  }

  private def unzipSet(trainingSet: Seq[ParkingDataSet]) = {
    trainingSet.filter(_.hasUsefulData)
      .map(_.toMlTrainingTuple)
      .unzip
  }

}