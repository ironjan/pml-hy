package controllers

import javax.inject._

import org.joda.time.DateTime
import play.api._
import play.api.libs.json.Json
import play.api.mvc._
import repository.RawParkingDataRepository
import services.crawler.{PaderbornCrawler, PaderbornCrawlerImpl, RawParkingDataSet}

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * This controller creates an `Action` to handle HTTP requests to the
  * application's home page.
  */
@Singleton
class HomeController @Inject() (crawler: PaderbornCrawler) (repo: RawParkingDataRepository)
  extends Controller {

  /**
    * Create an Action to render an HTML page with a welcome message.
    * The configuration in the `routes` file means that this method
    * will be called when the application receives a `GET` request with
    * a path of `/`.
    */
  def index = Action.async { implicit request =>
    val start = System.currentTimeMillis()
    crawler.crawl
    val crawlingTime = System.currentTimeMillis() - start

    repo.getAll
      .map(crawledSets => {
        val count = crawledSets.count(_ => true)
        val msg = s"Crawled current set in ${crawlingTime}ms, $count sets in total."
        Ok(views.html.index(msg))
      })
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
