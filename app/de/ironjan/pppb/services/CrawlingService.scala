package de.ironjan.pppb.services

import java.time.Clock
import javax.inject._

import akka.actor.{Actor, _}
import de.ironjan.pppb.services.crawler.PaderbornCrawler
import play.api.Logger
import play.api.inject.ApplicationLifecycle

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
    5 minutes,
    crawlingActor,
    Event)

}
