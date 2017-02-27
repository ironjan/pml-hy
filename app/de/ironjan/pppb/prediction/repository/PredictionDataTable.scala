package de.ironjan.pppb.prediction.repository

import com.github.tototoshi.slick.PostgresJodaSupport._
import de.ironjan.pppb.core.BaseTable
import de.ironjan.pppb.core.model.ParkingDataSet
import de.ironjan.pppb.prediction.model.PredictionResult
import slick.driver.PostgresDriver.api._

// TODO create migration for predictions table
class PredictionDataTable(_tableTag: Tag) extends BaseTable[PredictionResult](_tableTag, Some("public"), "predictions") {

  def * = (predictedTime, avgAbsError, prediction, regressionClass, id, isDeleted) <> (PredictionResult.tupled, PredictionResult.unapply)

  def ? = (Rep.Some(predictedTime), Rep.Some(avgAbsError), Rep.Some(prediction), Rep.Some(regressionClass),
    Rep.Some(id), Rep.Some(isDeleted)).shaped.<>({ r =>
    import r._;
    _1.map(_ => PredictionResult.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get)))
  }, (_: Any) => throw new Exception("Inserting into ? projection not supported."))

  val predictedTime: Rep[org.joda.time.DateTime] = column[org.joda.time.DateTime]("predicted_time")
  val avgAbsError: Rep[Double] = column[Double]("avg_abs_error", O.Length(255, varying = true))
  val prediction: Rep[Double] = column[Double]("prediction")
  val regressionClass: Rep[String] = column[String]("regression_class")

  override val id: Rep[Long] = column[Long]("id", O.AutoInc, O.PrimaryKey)
  override val isDeleted: Rep[Boolean] = column[Boolean]("is_deleted", O.Default(false))

}