package services

import java.time.Clock
import javax.inject._

import akka.actor.{Actor, _}
import play.api.Logger
import play.api.inject.ApplicationLifecycle
import services.dbcleaner.DBCleaner

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

/**
 * Implements a service that regularly cleans the DB.
 */
 @Singleton
class DBCleaningService @Inject()(dbCleaner: DBCleaner,
                                  clock: Clock,
                                  appLifecycle: ApplicationLifecycle) {
  val system = ActorSystem("DBCleaningSystem")
  val Event = "Clean"

  Logger.info(s"Started application. Setting up CrawlingService.")


  val cleaningActor = system.actorOf(Props(new Actor {
    def receive = {
      case Event => {
        dbCleaner.removeUnneededEntries
        }
      }
      }))

  system.scheduler.schedule(10 seconds,
    60 minutes,
    cleaningActor,
    Event)

}
