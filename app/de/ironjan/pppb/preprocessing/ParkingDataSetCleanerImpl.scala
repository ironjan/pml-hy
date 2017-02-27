package de.ironjan.pppb.preprocessing

import com.google.inject.Inject
import de.ironjan.pppb.core.model.ParkingDataSet
import de.ironjan.pppb.core.model.DateTimeHelper._
import de.ironjan.pppb.core.repository.ParkingDataRepository
import org.joda.time.DateTime
import play.api.Logger

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.language.postfixOps
import scala.util.Try
/**
  * Implementation of Cleaner for ParkingDataSet
  */
class ParkingDataSetCleanerImpl @Inject()(parkingDataRepository: ParkingDataRepository)
  extends Cleaner[ParkingDataSet] {
  override def cleanDatabase: Unit = {
    removeUnneededEntries
    updateModels
  }

  def removeUnneededEntries: Unit = {
    Logger.info("removeUnneededEntries called")
    parkingDataRepository
      .getAll
      .flatMap(ds =>
          Future.sequence(
              ds.filter(_.isDeleteable)
            .map(d => parkingDataRepository.deleteById(d.id))))
      .map(_.sum)
      .foreach(sum => Logger.debug(s"Deleted $sum entries"))
  }
  private def updateModels = {
    Logger.info("updateModels called")
    parkingDataRepository
      .getAll
      .flatMap(ds =>
          Future.sequence(
              ds.filterNot(_.isRecentModel)
            .map(d => {Logger.debug(s"Cleaning $d"); cleanEntry(d)})
            .map(d => parkingDataRepository.updateById(d.id, d))))
      .map(_.sum)
      .foreach(sum => Logger.debug(s"Cleaned $sum entries."))
  }

  override def cleanEntry(t: ParkingDataSet): ParkingDataSet = {
    t.modelVersion match {
      case None => cleanToRecent(t)
      case Some(0) => cleanToRecent(t)
      case Some(1) => cleanToRecent(t)
      case Some(ParkingDataSet.recentModelVersion) => t
      case Some(version) => {
        Logger.warn(s"Unknown model version $version")
        t
      }
    }
  }

  private def cleanToRecent(t: ParkingDataSet) = {
    val crawlingTime = t.crawlingTime
    val (hourOfDay, minuteOfHour, dayOfWeek, dayOfMonth, weekOfMonth, weekOfYear) = crawlingTime.explode

    val free = Try(t.freeRaw.toInt).toOption
    val capacity = Try(t.capacityRaw.toInt).toOption

    ParkingDataSet(crawlingTime,
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
  }

}
