package controllers

import javax.inject._

import org.joda.time.DateTime
import play.api._
import play.api.libs.json.Json
import play.api.mvc._
import repository.RawParkingDataRepository
import services.crawler.{PaderbornCrawler, PaderbornCrawlerImpl, RawParkingDataSet}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * This controller creates an `Action` to handle HTTP requests to the
  * application's home page.
  */
@Singleton
class HomeController @Inject() (crawler: PaderbornCrawler,
                                repo: RawParkingDataRepository)
  extends Controller {

  def index = Action.async { implicit request =>
    repo.countAll.map{count =>
      Ok(views.html.index(count) )
    }
  }

  def all_crawled = Action.async { implicit request =>
    val repo = new RawParkingDataRepository
    repo.getAll
      .map(crawledSets =>
        Ok(Json.toJson(crawledSets)))
  }

  def working_data_crawled = Action.async { implicit request =>
    repo.getAll
      .map { crawledSets =>
        val filtered = crawledSets
          .filter(d => scala.util.Try(d.used.toInt).isSuccess)

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
