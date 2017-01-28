package services.crawler

import core._

import org.joda.time.DateTime
import play.api.libs.json.Json

/**
  */
case class RawParkingDataSet(dateTime: DateTime,
                             name: String,
                             inUSe: String,
                             capacity: String,
                             id: Long = -1,
                             isDeleted: Boolean = false)
  extends BaseEntity

object RawParkingDataSet {
  implicit val rawParkingDataSetWrites = Json.writes[RawParkingDataSet]
  implicit val rawParkingDataSetReads = Json.reads[RawParkingDataSet]

  def tupled = (RawParkingDataSet.apply _).tupled
}