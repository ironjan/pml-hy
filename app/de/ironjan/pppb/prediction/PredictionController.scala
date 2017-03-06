package de.ironjan.pppb.prediction
import javax.inject.{Inject, Singleton}

import play.api.mvc._
import play.api.mvc.Results._
import play.api.libs.json.Json
import de.ironjan.pppb.core.model.DateTimeHelper._
import de.ironjan.pppb.training.Trainer

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
    predictionService.getAll.map(ps => Ok(Json.toJson(ps.filter(_.predictedTime.isLessThan2DaysOld).sortBy(_.predictedTime.getMillis()))))
  }

  def do_something_great =  Action.async{ implicit request =>
    predictionService.doSomethingGreat.map(s => Ok(Json.toJson(s)))
  }

  def importances = Action.async { implicit  request =>
    predictionService.importances.map(x => Ok(Json.toJson(x.mkString(", "))))
  }
}
