package de.ironjan.pppb.controllers

import javax.inject._

import de.ironjan.pppb.repository.ParkingDataRepository
import de.ironjan.pppb.services.crawler.PaderbornCrawler
import org.joda.time.DateTime
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * This controller creates an `Action` to handle HTTP requests to the
  * application's home page.
  */
@Singleton
class HomeController @Inject() (crawler: PaderbornCrawler,
                                repo: ParkingDataRepository)
  extends Controller {

  def index = Action.async { implicit request =>
    repo.countAll.map{count =>
      Ok(de.ironjan.pppb.views.html.index(count) )
    }
  }

  def all_crawled = Action.async { implicit request =>
    val repo = new ParkingDataRepository
    repo.getAll
      .map(crawledSets =>
        Ok(Json.toJson(crawledSets)))
  }

  def working_data_crawled = Action.async { implicit request =>
    repo.getAll
      .map { crawledSets =>
        val filtered = crawledSets
          .filter(d => d.isRecentModel && d.hasUsefulData)

        Ok(Json.toJson(filtered))
      }
  }

  def crawling_time_history = Action.async {implicit request =>
    repo.getAll
      .map {crawledSets =>
        Ok(Json.toJson(
          crawledSets.map(d => new DateTime(d.crawlingTime)).distinct
        ))
      }
  }
}
