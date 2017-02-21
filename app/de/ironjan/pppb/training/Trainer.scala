package de.ironjan.pppb.training

import com.google.inject.Inject
import de.ironjan.pppb.core.repository.ParkingDataRepository
import play.api.Logger
import smile.regression.RegressionTree

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by Jan Lippert on 19.02.2017.
  */
class Trainer @Inject() (parkingDataRepository: ParkingDataRepository){
  def doSomething = {
    Logger.debug(s"Started training.")

    val unzipped =
    Await.result(parkingDataRepository.getAll
      .map(ds =>
          // FIXME just using get!
          ds
      .filter(_.hasUsefulData)
      .map(d => (Array(d.hourOfDay.get.toDouble, d.minuteOfHour.get.toDouble, d.dayOfWeek.get.toDouble,
          d.dayOfMonth.get.toDouble, d.weekOfMonth.get.toDouble, d.weekOfYear.get.toDouble), d.free.get.toDouble))
      .unzip),
        Duration.Inf)

    // TODO better way for double .toArray?
    val x = unzipped._1.toArray
    val y = unzipped._2.toArray


    val beforeTraining = System.currentTimeMillis
    Logger.debug(s"Prepared training data.")

    val regressionTree = smile.regression.cart(x,y,maxNodes = 100)

    val trainingTime = System.currentTimeMillis - beforeTraining
    Logger.debug(s"Got regression tree in ${trainingTime}ms.")
    Logger.debug(s"Results: ${regressionTree.importance()} ${regressionTree.maxDepth()}.")
  }
}
