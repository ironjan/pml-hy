package services.enhancer

import java.time.Clock
import javax.inject.{Inject, Singleton}

import akka.actor.{Actor, ActorSystem, Props}
import play.api.Logger
import play.api.inject.ApplicationLifecycle
import services.crawler.PaderbornCrawler

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.language.postfixOps


@Singleton
class CleanerService @Inject()(cleaner: ParkingDataSetCleanerImpl,
                               clock: Clock,
                               appLifecycle: ApplicationLifecycle)  {

  val system = ActorSystem("CleaningSystem")
  val Event = "Clean"


  Logger.info(s"Started application. Setting up CleanerService.")


  val crawlingActor = system.actorOf(Props(new Actor {
    def receive = {
      case Event => {
        println(s"Triggering clean…")
        cleaner.cleanDatabase
      }
    }
  }))

  system.scheduler.schedule(15 seconds,
    5 minutes,
    crawlingActor,
    Event)
}
