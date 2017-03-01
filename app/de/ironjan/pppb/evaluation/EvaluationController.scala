package de.ironjan.pppb.evaluation

import javax.inject.{Inject, Singleton}

import de.ironjan.pppb.core.ParkingDataSetJson
import de.ironjan.pppb.core.model.ParkingDataSet
import de.ironjan.pppb.core.repository.ParkingDataRepository
import de.ironjan.pppb.crawling.PaderbornCrawler
import de.ironjan.pppb.prediction.model.PredictionResult
import de.ironjan.pppb.prediction.repository.PredictionDataRepository
import play.api.libs.json.Json
import play.api.mvc.Action
import play.api.mvc.Results._
import de.ironjan.pppb.core.model.DateTimeHelper._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import de.ironjan.pppb.core.model.DateTimeHelper._

@Singleton
class EvaluationController @Inject()(parkingDataRepo: ParkingDataRepository,
                                     predictionDataRepo: PredictionDataRepository) {

  def getAll = Action.async { implicit request =>
    computeTmpEvalResults.map(xs => Ok(Json.toJson(xs)))
  }

  def getSimplified = Action.async {implicit request =>
    computeSimplifiedResults.map(ts =>
    Ok(Json.toJson(ts)))
  }


  def getSimplifiedLatest = Action.async { implicit request =>
    computeSimplifiedResults.map(ts => Ok(Json.toJson(ts.filter(_.dateTime.isLessThan1DayOld))))
  }
  def getStats = Action.async { implicit  request =>
    computeSimplifiedResults.map(ts =>
    {
      val deltas = ts.map(_.delta).toArray
      val (mean, std) = meanStd(deltas)
      Ok(Json.toJson(Map("mean" -> mean, "std" -> std, "n" -> deltas.length.toDouble)))
    }
    )
  }

  /**
    * http://www.scalaformachinelearning.com/2015/10/recursive-mean-and-standard-deviation.html
    * @param x
    * @return
    */
  def meanStd(x: Array[Double]): (Double, Double) ={

    @scala.annotation.tailrec
    def meanStd(
                 x: Array[Double],
                 mu: Double,
                 Q: Double,
                 count: Int): (Double, Double) =
      if (count >= x.length) (mu, Math.sqrt(Q/x.length))
      else {
        val newCount = count +1
        val newMu = x(count)/newCount + mu * (1.0 - 1.0/newCount)
        val newQ = Q + (x(count) - mu)*(x(count) - newMu)
        meanStd(x, newMu, newQ, newCount)
      }

    meanStd(x, 0.0, 0.0, 0)
  }

  private def computeSimplifiedResults = {
    computeTmpEvalResults.map(ts =>
      ts.map(t  => SimplifiedEvalResult(t.prediction.predictedTime, t.prediction.prediction, t.parkingDataSetJson.free.get)))
  }

  private def computeTmpEvalResults = {
    // TODO actually use brain before rewrite
    val explodedPredictions = Await.result(predictionDataRepo.getAll, Duration.Inf)
      .toList
      .sortBy(p => p.predictedTime.getMillis)
      .map(p => (p.predictedTime.explode, p))

    val keys = explodedPredictions.map(_._1).toSet

    parkingDataRepo.getAll.map(ds =>
      ds.filter(_.hasUsefulData)
        .sortBy(_.crawlingTime.getMillis)
        .map(d => (d.crawlingTime.explode, ParkingDataSetJson.from(d)))
        .filter(ed => keys.contains(ed._1))
        .zip(explodedPredictions)
        .map(pair => {
          val prediction = pair._2._2
          val actual = pair._1._2
          val delta = Math.abs(actual.free.get - prediction.prediction)
          TmpEvalResult(prediction, actual, delta)
        }))
  }
}
