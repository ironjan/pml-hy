package de.ironjan.pppb.evaluation

import javax.inject.{Inject, Singleton}

import de.ironjan.pppb.core.{MeanStd, ParkingDataSetJson}
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
import de.ironjan.pppb.core

@Singleton
class EvaluationController @Inject()(parkingDataRepo: ParkingDataRepository,
                                     predictionDataRepo: PredictionDataRepository) {

  def getAll = Action.async { implicit request =>
    computeTmpEvalResults.map(xs => Ok(Json.toJson(xs.sortBy(_.prediction.predictedTime.getMillis))))
  }

  def getSimplified = Action.async { implicit request =>
    computeSimplifiedResults.map(ts =>
      Ok(Json.toJson(ts.sortBy(_.dateTime.getMillis))))
  }


  def getSimplifiedLatest = Action.async { implicit request =>
    computeSimplifiedResults.map(ts => Ok(Json.toJson(ts.filter(_.dateTime.isLessThan2DaysOld).sortBy(_.dateTime.getMillis))))
  }

  def getStats = Action.async { implicit request =>
    computeSimplifiedResults.map(ts => {
      val deltas = ts.map(_.delta).toArray
      val (mean, std) = MeanStd.meanStd(deltas)
      Ok(Json.toJson(Map("mean" -> mean, "std" -> std, "n" -> deltas.length.toDouble)))
    }
    )
  }


  private def computeSimplifiedResults = {
    computeTmpEvalResults.map(ts =>
      ts.map(t =>
        SimplifiedEvalResult(t.prediction.predictedTime, t.prediction.prediction, t.parkingDataSetJson.free.get, t.delta)))
  }

  private def computeTmpEvalResults = {
    // TODO actually use brain before rewrite
    val explodedPredictions = Await.result(predictionDataRepo.getAll, Duration.Inf)
      .toList
      .sortBy(p => p.predictedTime.getMillis)
      .map(p => (p.predictedTime.explode, p))

    val keys = explodedPredictions.map(_._1).toSet

    parkingDataRepo.getAll.map(ds => {
      val crawledAndPreparedForPairing = ds.filter(_.hasUsefulData)
        .sortBy(_.crawlingTime.getMillis)
        .map(d => (d.crawlingTime.explode, ParkingDataSetJson.from(d)))
        .filter(ed => keys.contains(ed._1))

      crawledAndPreparedForPairing.flatMap(c => findPartner(c,explodedPredictions))
    })
  }



  private def findPartner(dTuple: ( (Int, Int, Int, Int, Int, Int), ParkingDataSetJson), explodedPredictions: List[((Int, Int, Int, Int, Int, Int), PredictionResult)])
  : Option[TmpEvalResult]= {
    explodedPredictions.find(_._1 == dTuple._1).map(x => {
      val predictionObject = x._2
      val actualParkingData = dTuple._2

      val predictedY = predictionObject.prediction
      val actualY = actualParkingData.free.get

      val delta = Math.abs(predictedY - actualY)
      TmpEvalResult(x._2, dTuple._2, delta)
    })
  }


}
