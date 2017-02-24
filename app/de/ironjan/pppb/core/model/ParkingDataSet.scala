package de.ironjan.pppb.core.model

import de.ironjan.pppb.core.BaseEntity
import org.joda.time.DateTime
import play.api.libs.json.Json
import smile.data.{Attribute, NominalAttribute}

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

  val recentModelVersion = 2

  implicit class ParkingDataSetOps(parkingDataSet: ParkingDataSet) {
    def isRecentModel = parkingDataSet.modelVersion.contains(recentModelVersion)

    def hasUsefulData = parkingDataSet.free.nonEmpty

    def isPreCrawlerFix = {
      val crawlingDateFix = new DateTime().withYear(2017).withMonthOfYear(2).withDayOfMonth(15)
      parkingDataSet.crawlingTime.isBefore(crawlingDateFix)
    }
    
    def isDeleteable = isRecentModel && !hasUsefulData && isPreCrawlerFix

    // FIXME just using get!
    def toMlTrainingTuple =
      (
        Array(
          parkingDataSet.hourOfDay.get.toDouble,
          parkingDataSet.minuteOfHour.get.toDouble,
          parkingDataSet.dayOfWeek.get.toDouble,
          parkingDataSet.dayOfMonth.get.toDouble,
          parkingDataSet.weekOfMonth.get.toDouble,
          parkingDataSet.weekOfYear.get.toDouble),
        parkingDataSet.free.get.toDouble)
  }

  def attributes: Array[Attribute] =
    Array(
      new NominalAttribute("hourOfDay", Array(0 to 23).map(_.toString)),
      new NominalAttribute("minuteOfHours", Array(0 to 59).map(_.toString)),
      new NominalAttribute("dayOfWeek", Array(0 to 6).map(_.toString)),
      new NominalAttribute("dayOfMonth", Array(0 to 31).map(_.toString)),
      new NominalAttribute("weekOfMonth", Array(-5 to 5).map(_.toString)),
      new NominalAttribute("weekOfYear", Array(0 to 52).map(_.toString)))
}