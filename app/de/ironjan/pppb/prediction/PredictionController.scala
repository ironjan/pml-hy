package de.ironjan.pppb.prediction

import javax.inject.{Inject, Singleton}

import de.ironjan.pppb.training.Trainer
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class PredictionController @Inject()(trainer: Trainer) {

  def all_crawled = Action.async { implicit request =>
    Future.successful(Results.NotImplemented("NIY"))
  }

}
