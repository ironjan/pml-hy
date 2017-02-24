package de.ironjan.pppb.prediction

import java.time.Period
import javax.inject.{Inject, Singleton}

import de.ironjan.pppb.core.model.ParkingDataSet
import de.ironjan.pppb.core.repository.ParkingDataRepository
import de.ironjan.pppb.training.Trainer
import org.joda.time.{DateTime, DurationFieldType}
import play.api.mvc._
import de.ironjan.pppb.core.model.DateTimeOps._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class PredictionController @Inject()(repo: ParkingDataRepository,
                                     trainer: Trainer) {

  def all_crawled = Action.async { implicit request =>

    repo.getAll.map {ds =>
      val now = new DateTime();
      val futureTime = now.withFieldAdded(DurationFieldType.minutes(), 15)
      val futureTimeAsDoubleArray = futureTime.toPredictionQuery
      val filtered = ds.filter(_.hasUsefulData)
      val bestModel = trainer.findBestModel(filtered)

      bestModel.predict(futureTimeAsDoubleArray)
    }
    Future.successful(Results.NotImplemented("NIY"))
  }

}
