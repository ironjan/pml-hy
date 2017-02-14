package de.ironjan.pppb.core.model

import de.ironjan.pppb.core.BaseEntity
import org.joda.time.DateTime
import play.api.libs.json.Json

/**
  */
case class ParkingDataSet(crawlingTime: DateTime,
                          name: String,
                          freeRaw: String,
                          capacityRaw: String,
                          city: String,

                          id: Long = -1,
                          isDeleted: Boolean = false,

                          modelVersion: Option[Int] = Some(0),
                          hourOfDay: Option[Int] = None,
                          minuteOfHour: Option[Int] = None,

                          dayOfWeek: Option[Int] = None,
                          dayOfMonth: Option[Int] = None,

                          weekOfMonth: Option[Int] = None,
                          weekOfYear: Option[Int] = None,

                          free: Option[Int] = None,
                          capacity: Option[Int] = None)
  extends BaseEntity

object ParkingDataSet {
  implicit val rawParkingDataSetWrites = Json.writes[ParkingDataSet]
  implicit val rawParkingDataSetReads = Json.reads[ParkingDataSet]

  def tupled = (ParkingDataSet.apply _).tupled

  val recentModelVersion = 1

  implicit class ParkingDataSetOps(parkingDataSet: ParkingDataSet){
    def isRecentModel = parkingDataSet.modelVersion.contains(recentModelVersion)
    def hasUsefulData = parkingDataSet.free.nonEmpty
    def isDeleteable = isRecentModel && !hasUsefulData

  }

}