package de.ironjan.pppb.prediction

import javax.inject.{Inject, Singleton}

import akka.actor.{Actor, ActorSystem, Props}
import de.ironjan.pppb.core.repository.ParkingDataRepository
import de.ironjan.pppb.training.Trainer
import org.joda.time.{DateTime, DurationFieldType}
import smile.regression.Regression
import de.ironjan.pppb.core.model.DateTimeHelper._
import de.ironjan.pppb.prediction.model.PredictionResult
import de.ironjan.pppb.prediction.repository.PredictionDataRepository
import play.api.Logger

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

@Singleton
class PredictionService @Inject()(parkingDataRepo: ParkingDataRepository,
                                  predictionDataRepo: PredictionDataRepository,
                                  trainer: Trainer) {
  def getAll = predictionDataRepo.getAll

  def importances = trainer.findImportances

  val system = ActorSystem("PredictionSystem")
  val PredictionEvent = "Predict"

  Logger.info(s"Started application. Setting up PredictionService.")


  val actor = system.actorOf(Props(new Actor {
    def receive = {
      case PredictionEvent => {
        Logger.debug("Doing a new prediciton")
        onDemandPrediction.map(predictionDataRepo.save)
          .foreach(s => Logger.debug(s"Saved prediction: $s"))
      }
    }
  }))

  system.scheduler.schedule(30 seconds,
    1 minutes,
    actor,
    PredictionEvent)

  def onDemandPrediction = {
    trainer.findBestModel.map {
      case (avgAbsError: Double, bestModel: Regression[Array[Double]]) =>

        val timeIn15Minutes = new DateTime().withFieldAdded(DurationFieldType.minutes(), 15)
        val prediction = bestModel.predict(timeIn15Minutes.toPredictionQuery)

        val normalizedPrediction = if(prediction < 0) { 0 }
                                   else if(prediction > 500) { 500 }
                                   else { prediction }

        val regressionName = bestModel.getClass.getSimpleName

        PredictionResult(timeIn15Minutes, avgAbsError, normalizedPrediction, regressionName)
    }
  }

  def doSomethingGreat = {
    trainer.doSomethingGreat
      .map { case (avgAbsError: Double, bestModel: Regression[Array[Double]]) =>
        val printableModel = trainer.toPrintable(bestModel)
        s"Found: ${printableModel} with avgAbsError $avgAbsError"
      }
  }
}
