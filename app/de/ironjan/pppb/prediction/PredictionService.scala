package de.ironjan.pppb.prediction

import javax.inject.Inject

import de.ironjan.pppb.core.repository.ParkingDataRepository
import de.ironjan.pppb.training.Trainer
import org.joda.time.{DateTime, DurationFieldType}
import smile.regression.Regression
import de.ironjan.pppb.core.model.DateTimeHelper._

class PredictionService  @Inject()(repo: ParkingDataRepository,
                                   trainer: Trainer){


def onDemandPrediction = {
  repo.getAll.map {ds =>
    val (avgAbsError: Double, bestModel: Regression[Array[Double]]) = trainer.findBestModel(ds.filter(_.hasUsefulData))

    val timeIn15Minutes = new DateTime().withFieldAdded(DurationFieldType.minutes(), 15)
    val prediction = bestModel.predict(timeIn15Minutes.toPredictionQuery)

    val regressionName = bestModel.getClass.getSimpleName

    PredictionResult(timeIn15Minutes, avgAbsError, prediction, regressionName)
  }
}
}
