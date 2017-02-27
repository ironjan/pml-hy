package de.ironjan.pppb.evaluation

import de.ironjan.pppb.core.ParkingDataSetJson
import org.joda.time.DateTime
import play.api.libs.json.Json

case class SimplifiedEvalResult(dateTime: DateTime, predicted: Double, actual: Double){
  def delta = Math.abs(actual - predicted)
}

object SimplifiedEvalResult{
  implicit val simplifiedEvalResultWrites = Json.writes[SimplifiedEvalResult]
  def tupled = (SimplifiedEvalResult.apply _).tupled

}
