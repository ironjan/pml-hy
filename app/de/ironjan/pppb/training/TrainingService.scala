package de.ironjan.pppb.training

import java.time.Clock
import javax.inject.{Inject, Singleton}

import akka.actor.{Actor, ActorSystem, Props}
import play.api.Logger
import play.api.inject.ApplicationLifecycle

import scala.language.postfixOps

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._


@Singleton
class TrainingService @Inject()(trainer: Trainer,
                                appLifecycle: ApplicationLifecycle)  {

  val Event = "TrainingTime"
  val system = ActorSystem("TrainingServiceSystem")

  val actor = system.actorOf(Props(new Actor {
    def receive = {
      case Event => {
        trainer.timeModelEvaluation
      }
    }
  }))

  system.scheduler.schedule(0 seconds,
    30 minutes,
    actor,
    Event)
}
