package de.ironjan.pppb

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
class IndexController @Inject()(crawler: PaderbornCrawler,
                                repo: ParkingDataRepository)
  extends Controller {

  def index = Action.async { implicit request =>
    repo.countAll.map { count =>
      Ok(de.ironjan.pppb.views.html.index(count))
    }
  }

}
