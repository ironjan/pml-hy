package de.ironjan.pppb.training

import java.lang.IllegalArgumentException

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
import scala.util.Try

/**
  * Created by Jan Lippert on 19.02.2017.
  */
class Trainer @Inject()(parkingDataRepository: ParkingDataRepository) {
  val shrinkageStepWidth = 0.05


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

    var counter = 0
    // TODO just using get on option
    val best = Stream.concat(
      {
        Logger.debug("toFullTrainingTuple")
        evaluateModels(splitSet, splitSet._1.head.capacity.get, toFullTrainingTuple)
      },
      {
        Logger.debug("toHourMinutesTuple")
        evaluateModels(splitSet, splitSet._1.head.capacity.get, toHourMinutesTuple)
      },
      {
        Logger.debug("toMinutesOfDayTuple")
        evaluateModels(splitSet, splitSet._1.head.capacity.get, toMinutesOfDayTuple)
      }
    )
      .minBy(p => {
          Logger.debug(s"Min by for $p")
        counter += 1
          p._1
      })

    Logger.info(s"Found that $best is the best ($counter checked in total)")
    best
  }

  private def evaluateModels(ds: (Seq[ParkingDataSet], Seq[ParkingDataSet]), capacity: Int, modelMaker: ParkingDataSet =>  (Array[Double], Double)) = {
    val trainingSet = ds._1
    val testSet = ds._2
    Logger.debug(s"Training subset of length ${trainingSet.length} and test set of length ${testSet.length}.")

    val unzipped = unzipSet(trainingSet, modelMaker)

    // TODO better way for double .toArray?
    val x = unzipped._1.toArray
    val y = unzipped._2.toArray


    Logger.debug(s"Prepared training data.")

    val shrinkageSteps = (1/shrinkageStepWidth - 1).toInt
    Stream.concat(
      Stream(evaluate(smile.regression.cart(x, y, 100), testSet, modelMaker),
        try{
          evaluate(smile.regression.randomForest(x, y), testSet, modelMaker)
        }catch {case e: IllegalArgumentException => (Double.PositiveInfinity, null)}
      ),
      Stream.tabulate(shrinkageSteps) { i => (i + 1) * shrinkageStepWidth }.map(s => evaluate(smile.regression.gbm(x, y, shrinkage = s), testSet, modelMaker)))
  }

  private def evaluate(regression: Regression[Array[Double]], T: Seq[ParkingDataSet], modelMaker: ParkingDataSet =>  (Array[Double], Double)) = {
    try{
      val xStars = unzipSet(T, modelMaker)._1
      val yStars = unzipSet(T, modelMaker)._2

      val aes = xStars.map(regression.predict)
        .zip(yStars)
        .map(p => Math.abs(p._1 - p._2))

      val mae = aes.sum / aes.length
//      Logger.debug(s"${regression} had a mean average error of $mae.")
      (mae, regression)
    } catch {
      case e: IllegalArgumentException => {
        Logger.warn(e.getMessage)
        (Double.PositiveInfinity, null)
      }
    }
  }

  private def unzipSet(trainingSet: Seq[ParkingDataSet], modelMaker: ParkingDataSet =>  (Array[Double], Double)) = {
    trainingSet.filter(_.hasUsefulData)
      .map(modelMaker)
      .unzip
  }

  // FIXME just using get!
  private def toFullTrainingTuple(parkingDataSet: ParkingDataSet): (Array[Double], Double) =
    (
      Array(
        parkingDataSet.hourOfDay.get.toDouble,
        parkingDataSet.minuteOfHour.get.toDouble,
        parkingDataSet.dayOfWeek.get.toDouble,
        parkingDataSet.dayOfMonth.get.toDouble,
        parkingDataSet.weekOfMonth.get.toDouble,
        parkingDataSet.weekOfYear.get.toDouble),
      parkingDataSet.free.get.toDouble)

  private def toHourMinutesTuple(parkingDataSet: ParkingDataSet): (Array[Double], Double) =
    (Array(parkingDataSet.hourOfDay.get, parkingDataSet.minuteOfHour.get), parkingDataSet.free.get)

  private def toMinutesOfDayTuple(parkingDataSet: ParkingDataSet): (Array[Double], Double) =
    (Array(parkingDataSet.hourOfDay.get * 60 + parkingDataSet.minuteOfHour.get), parkingDataSet.free.get)
}