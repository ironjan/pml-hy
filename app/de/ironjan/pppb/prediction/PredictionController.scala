package de.ironjan.pppb.prediction
import javax.inject.{Inject, Singleton}

import play.api.mvc._
import play.api.mvc.Results._
import play.api.libs.json.Json
import de.ironjan.pppb.core.model.DateTimeHelper._
import de.ironjan.pppb.training.Trainer
import play.api.Logger

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class PredictionController @Inject()(predictionService: PredictionService) {

  def predict = Action.async { implicit request =>
    predictionService.onDemandPrediction.map(r => Ok(Json.toJson(r)))
  }

  def all_predictions = Action.async {implicit  request =>
    predictionService.getAll.map(ps => Ok(Json.toJson(ps)))
  }
  def all_predictions_latest = Action.async {implicit  request =>
    predictionService.getAll.map{      ps =>
      val start = System.currentTimeMillis()
      val filtered = ps.filter(_.predictedTime.isLessThan1DayOld)

      val filterTimestamp= System.currentTimeMillis()
      Logger.debug(s"filtered: ${filterTimestamp - start}ms")
      val sorted = filtered.sortBy(_.predictedTime.getMillis())
      Logger.debug(s"sorted:   ${System.currentTimeMillis() - filterTimestamp}ms")
      val json = Json.toJson(sorted)
      Logger.debug(s"jsoned:   ${System.currentTimeMillis() - filterTimestamp}ms")
      Ok(json)
    }
  }

  def do_something_great =  Action.async{ implicit request =>
    predictionService.doSomethingGreat.map(s => Ok(Json.toJson(s)))
  }

  def importances = Action.async { implicit  request =>
    predictionService.importances.map(x => Ok(Json.toJson(x.mkString(", "))))
  }
}
