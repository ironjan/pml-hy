package de.ironjan.pppb.prediction
import javax.inject.{Inject, Singleton}

import play.api.mvc._
import play.api.mvc.Results._
import play.api.libs.json.Json

import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class PredictionController @Inject()(predictionService: PredictionService) {

  def predict = Action.async { implicit request =>
    predictionService.onDemandPrediction.map(r => Ok(Json.toJson(r)))
  }

}
