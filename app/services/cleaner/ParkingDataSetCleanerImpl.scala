package services.cleaner

import com.google.inject.Inject
import org.joda.time.Weeks
import play.api.Logger
import repository.ParkingDataRepository
import services.crawler.ParkingDataSet

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util.Try

/**
  * Implementation of Cleaner for ParkingDataSet
  */
class ParkingDataSetCleanerImpl @Inject()(parkingDataRepository: ParkingDataRepository)
  extends Cleaner[ParkingDataSet] {
  override def cleanDatabase: Unit = {
    parkingDataRepository
      .getAll
      .map(_.filterNot(_.isRecentModel).map(cleanEntry).foreach(d => Await.result(parkingDataRepository.save(d), 5 seconds)))
  }

  override def cleanEntry(t: ParkingDataSet): ParkingDataSet = {
    Logger.debug(s"Cleaning $t.")
    t.modelVersion match {
      case None => cleanToRecent(t)
      case Some(0) => cleanToRecent(t)
      case Some(ParkingDataSet.recentModelVersion) => {
        Logger.debug(s"$t is already most recent model.")
        t
      }
      case Some(version) => {
        Logger.warn(s"Unknown model version $version")
        t
      }
    }
  }

  private def cleanToRecent(t: ParkingDataSet) = {
    val crawlingTime = t.crawlingTime
    val hourOfDay = crawlingTime.getHourOfDay
    val minuteOfHour = crawlingTime.getMinuteOfHour
    val dayOfWeek = crawlingTime.getDayOfWeek
    val dayOfMonth = crawlingTime.getDayOfMonth
    val weekOfMonth = Weeks.weeksBetween(crawlingTime, crawlingTime.withDayOfMonth(1)).getWeeks + 1
    val weekOfYear = crawlingTime.getWeekOfWeekyear

    val free = Try(t.freeRaw.toInt).toOption
    val capacity = Try(t.capacityRaw.toInt).toOption

    val cleaned = ParkingDataSet(crawlingTime,
      t.name,
      t.freeRaw,
      t.capacityRaw,
      t.city,
      t.id,
      t.isDeleted,
      Some(ParkingDataSet.recentModelVersion),
      Some(hourOfDay),
      Some(minuteOfHour),
      Some(dayOfWeek),
      Some(dayOfMonth),
      Some(weekOfMonth),
      Some(weekOfYear),
      free,
      capacity)

    Logger.debug(s"Cleaned ParkingDataSet: $cleaned")

    cleaned
  }
}
