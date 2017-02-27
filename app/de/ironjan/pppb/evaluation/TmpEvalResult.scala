package de.ironjan.pppb.evaluation

import de.ironjan.pppb.core.ParkingDataSetJson
import de.ironjan.pppb.prediction.model.PredictionResult
import play.api.libs.json.Json

case class TmpEvalResult (prediction: PredictionResult, parkingDataSetJson: ParkingDataSetJson, delta:Double)

object TmpEvalResult {
  implicit val tmpEvalResultWrites = Json.writes[TmpEvalResult]
  def tupled = (TmpEvalResult.apply _).tupled
}