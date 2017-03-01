package de.ironjan.pppb.core

import javax.inject._

import de.ironjan.pppb.core.repository.ParkingDataRepository
import de.ironjan.pppb.crawling.PaderbornCrawler
import org.joda.time.DateTime
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * This controller creates an `Action` to handle HTTP requests to the
  * application's home page.
  */
@Singleton
class DataAPIController @Inject()(crawler: PaderbornCrawler,
                                  repo: ParkingDataRepository)
  extends Controller {

  def all_crawled = Action.async { implicit request =>
    val repo = new ParkingDataRepository
    repo.getAll
      .map(crawledSets =>
        Ok(Json.toJson(
            crawledSets.sortBy(_.crawlingTime.getMillis())
            .map(ParkingDataSetJson.from))))
  }

  def working_data_crawled = Action.async { implicit request =>
    repo.getAll
      .map { crawledSets =>
        Ok(Json.toJson(
          crawledSets.filter(d => d.isRecentModel && d.hasUsefulData)
          .sortBy(_.crawlingTime.getMillis())
          .map(ParkingDataSetJson.from)))
      }
  }

  def crawling_time_history = Action.async { implicit request =>
    repo.getAll
      .map { crawledSets =>
        Ok(Json.toJson(
          crawledSets.map(d => d.crawlingTime).distinct
        ))
      }
  }

  def csv = Action.async { implicit request =>
    repo.getAll
      .map { crawledSets =>
        crawledSets.sortBy(_.crawlingTime.getMillis())
          .map {
            def unifyStringLength(i: Int) = {
              val prefix = if (i < 10) "  "
              else if (i < 100) " "
              else ""
              prefix + i
            }

            d =>
          // TODO verify that get doesn't cause problems
              val hourOfDay = unifyStringLength(d.hourOfDay.get)
              val minuteOfHour = unifyStringLength(d.minuteOfHour.get)
              val dayOfWeek = unifyStringLength(d.dayOfWeek.get)
              val dayOfMonth = unifyStringLength(d.dayOfMonth.get)
              val weekOfYear = unifyStringLength(d.weekOfYear.get)
              val free = unifyStringLength(d.free.get)
              val capacity = unifyStringLength(d.capacity.get)

              (d.crawlingTime, hourOfDay, minuteOfHour, dayOfWeek, dayOfMonth, d.weekOfMonth.get, weekOfYear, free, capacity)
        }.map(t => t.productIterator.mkString(","))
          .mkString("\n")
      }.map(s => Ok(s.toString))
    //crawlingTime":1486933054571,"name":"P6 Libori-Galerie","freeRaw":"174","capacityRaw":"500","city":"Paderborn","id":602190,"isDeleted":false,"modelVersion":1,"hourOfDay":20,"minuteOfHour":57,"dayOfWeek":7,"dayOfMonth":12,"weekOfMonth":0,"weekOfYear":6,"free":174,"capacity
  }


}
