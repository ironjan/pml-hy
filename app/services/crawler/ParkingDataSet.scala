package services.crawler

import core._
import org.joda.time.DateTime
import play.api.libs.json.Json

/**
  */
case class ParkingDataSet(crawlingTime: DateTime,
                          name: String,
                          free: String,
                          capacity: String,
                          city: String,
                          id: Long = -1,
                          isDeleted: Boolean = false,
                          modelVersion: Option[Int] = Some(0),
                          hourOfDay: Option[Int] = None,
                          minuteOfHour: Option[Int] = None,
                          dayOfWeek: Option[Int] = None,
                          dayOfMonth: Option[Int] = None,
                          weekOfMonth: Option[Int] = None,
                          weekOfYear: Option[Int] = None)
  extends BaseEntity

object ParkingDataSet {
  implicit val rawParkingDataSetWrites = Json.writes[ParkingDataSet]
  implicit val rawParkingDataSetReads = Json.reads[ParkingDataSet]

  def tupled = (ParkingDataSet.apply _).tupled
}