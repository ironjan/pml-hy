package de.ironjan.pppb.prediction.model

import de.ironjan.pppb.core.{BaseEntity, JsonFormats}
import org.joda.time.DateTime
import play.api.libs.functional.syntax.{unlift, _}
import play.api.libs.json.{JsPath, Writes}

case class PredictionResult(predictedTime: DateTime,
                            avgAbsError: Double,
                            prediction: Double,
                            regressionClass: String,

                            id: Long = -1,
                            isDeleted: Boolean = false)
  extends BaseEntity


object PredictionResult{
  implicit val predictionResultJsonWrites: Writes[PredictionResult] = (
    (JsPath \ "predictedTime").write[DateTime](JsonFormats.jodaDateWrites) and
      (JsPath \ "avgAbsError").write[Double] and
      (JsPath \ "prediction").write[Double] and
      (JsPath \ "regressionClass").write[String] and
      (JsPath \ "id").write[Long] and
      (JsPath \ "isDeleted").write[Boolean]
  )(unlift(PredictionResult.unapply))

  def tupled = (PredictionResult.apply _).tupled

}
