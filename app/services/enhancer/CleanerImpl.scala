package services.enhancer

import org.joda.time.Weeks
import services.crawler.ParkingDataSet

import scala.util.Try

/**
  * Implementation of Cleaner for ParkingDataSet
  */
class CleanerImpl extends Cleaner[ParkingDataSet] {
  override def cleanDatabase: Unit = ???

  val recentModelVersion = Some(0)

  override def cleanEntry(t: ParkingDataSet): ParkingDataSet = {
    t.modelVersion match {
      case None => cleanToRecent(t)
      case `recentModelVersion` => t
    }
  }

  private def cleanToRecent(t: ParkingDataSet) = {
    val crawlingTime = t.crawlingTime
    val hourOfDay = crawlingTime.getHourOfDay
    val minuteOfHour = crawlingTime.getMinuteOfHour
    val dayOfWeek = crawlingTime.getDayOfWeek
    val dayOfMonth = crawlingTime.getDayOfMonth
    val weekOfMonth = Weeks.weeksBetween(crawlingTime, crawlingTime.withDayOfMonth(1)).getWeeks() + 1
    val weekOfYear = crawlingTime.getWeekOfWeekyear

    val free = Try(t.freeRaw.toInt).toOption
    val capacity = Try(t.capacityRaw.toInt).toOption

    ParkingDataSet(crawlingTime,
      t.name,
      t.freeRaw,
      t.capacityRaw,
      t.city,
      t.id,
      t.isDeleted,
      recentModelVersion,
      Some(hourOfDay),
      Some(minuteOfHour),
      Some(dayOfWeek),
      Some(dayOfMonth),
      Some(weekOfMonth),
      Some(weekOfYear),
      free,
      capacity)
  }
}
