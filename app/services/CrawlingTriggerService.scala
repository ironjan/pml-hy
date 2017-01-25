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

  var Tick = 0
  val tickActor = system.actorOf(Props(new Actor {
    val limit = 100
    def receive = {
      case tick: Int => {
        if(tick < limit) {
          println(s"tick $tick/$limit")
          system.scheduler.scheduleOnce(50 milliseconds,
            self,
            tick + 1)
          }else{
            context.stop(self)
            println(s"ticked $limit times. Not rescheduling")
          }

        } 
      }
      }))
  //This will schedule to send the Tick-message
  //to the tickActor after 0ms repeating every 50ms
  system.scheduler.scheduleOnce(50 milliseconds,
    tickActor,
    Tick)

}
