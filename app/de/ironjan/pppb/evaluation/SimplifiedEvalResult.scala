package de.ironjan.pppb.evaluation

import de.ironjan.pppb.core.ParkingDataSetJson
import de.ironjan.pppb.core.JsonFormats
import org.joda.time.DateTime
import play.api.libs.json.Json
import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Writes}

case class SimplifiedEvalResult(dateTime: DateTime, predicted: Double, actual: Double, delta: Double)

object SimplifiedEvalResult{
  implicit val simplifiedEvalResultWrites: Writes[SimplifiedEvalResult] = (
    (JsPath \ "dateTime").write[DateTime](JsonFormats.jodaDateWrites) and
      (JsPath \ "predicted").write[Double] and
      (JsPath \ "actual").write[Double] and
      (JsPath \ "delta").write[Double]
    ) (unlift(SimplifiedEvalResult.unapply))

  def tupled = (SimplifiedEvalResult.apply _).tupled

}
