package services

import java.time.{Clock, Instant}
import javax.inject._
import play.api.Logger
import play.api.inject.ApplicationLifecycle
import scala.concurrent.Future
import akka.actor.Actor
import akka.actor._
import scala.concurrent.duration._
import scala.concurrent._
import akka.event.Logging

import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Implements a service that triggers the crawler on regular intervals.
 */
 @Singleton
 class CrawlingTriggerService @Inject() (clock: Clock, appLifecycle: ApplicationLifecycle) {

  Logger.info(s"Started application. Setting up CrawlingTriggerService.")

  val system = ActorSystem("CrawlingSystem")

  val Event = "Crawl"

  val crawlingActor = system.actorOf(Props(new Actor {
    def receive = {
      case Event => {
          println(s"Triggering crawl...")
        } 
      }
      }))
  
  system.scheduler.schedule(0 milliseconds,
    5 minutes,
    crawlingActor,
    Event)

}
