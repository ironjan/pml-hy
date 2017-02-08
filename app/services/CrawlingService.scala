package services

import java.time.Clock
import javax.inject._

import akka.actor.{Actor, _}
import play.api.Logger
import play.api.inject.ApplicationLifecycle
import services.crawler.PaderbornCrawler

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

/**
 * Implements a service that triggers the crawler on regular intervals.
 */
 @Singleton
 class CrawlingService @Inject()(crawler: PaderbornCrawler,
                                 clock: Clock,
                                 appLifecycle: ApplicationLifecycle) {
  val system = ActorSystem("CrawlingSystem")
  val Event = "Crawl"

  Logger.info(s"Started application. Setting up CrawlingService.")


  val crawlingActor = system.actorOf(Props(new Actor {
    def receive = {
      case Event => {
          println(s"Triggering crawl…")
          crawler.crawl
        }
      }
      }))

  system.scheduler.schedule(0 milliseconds,
    1 minutes,
    crawlingActor,
    Event)

}
