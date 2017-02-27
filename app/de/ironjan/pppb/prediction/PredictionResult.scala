package de.ironjan.pppb.prediction

import de.ironjan.pppb.core.JsonFormats
import org.joda.time.DateTime
import play.api.libs.functional.syntax.unlift
import play.api.libs.json.{JsPath, Json, Writes}
import play.api.libs.functional.syntax._

case class PredictionResult(predictedTime: DateTime,
                            avgAbsError: Double,
                            prediction: Double,
                            regressionClass: String)

object PredictionResult{
  implicit val predictionResultJsonWrites: Writes[PredictionResult] = (
    (JsPath \ "predictedTime").write[DateTime](JsonFormats.jodaDateWrites) and
      (JsPath \ "avgAbsError").write[Double] and
      (JsPath \ "prediction").write[Double] and
      (JsPath \ "regressionClass").write[String]
  )(unlift(PredictionResult.unapply))
}
