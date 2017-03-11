package de.ironjan.pppb.evaluation

import java.text.SimpleDateFormat
import javax.inject.{Inject, Singleton}

import de.ironjan.pppb.core.{MeanStd, ParkingDataSetJson}
import de.ironjan.pppb.core.model.ParkingDataSet
import de.ironjan.pppb.core.repository.ParkingDataRepository
import de.ironjan.pppb.crawling.PaderbornCrawler
import de.ironjan.pppb.prediction.model.PredictionResult
import de.ironjan.pppb.prediction.repository.PredictionDataRepository
import play.api.libs.json.Json
import play.api.mvc.{Action, Results}
import play.api.mvc.Results._
import de.ironjan.pppb.core.model.DateTimeHelper._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import de.ironjan.pppb.core.model.DateTimeHelper._
import de.ironjan.pppb.core
import org.joda.time.format.DateTimeFormat

@Singleton
class EvaluationController @Inject()(parkingDataRepo: ParkingDataRepository,
                                     predictionDataRepo: PredictionDataRepository) {

  def getAll = Action.async { implicit request =>
    computeTmpEvalResults.map(xs => Ok(Json.toJson(xs)))
  }

  def getSimplified = Action.async { implicit request =>
    computeSimplifiedResults
      .map(ts => Ok(Json.toJson(ts)))
  }


  def getSimplifiedLatest = Action.async { implicit request =>
    computeSimplifiedResults.map(ts => Ok(Json.toJson(ts.filter(_.dateTime.isLessThan2DaysOld))))
  }

  def getSimplifiedAsCsv = Action.async { implicit request =>
      computeSimplifiedResults
        .map { ts =>
          ts.map { t =>
            val date = DateTimeFormat.forPattern("YYYY-MM-dd").print(t.dateTime)
            val time = DateTimeFormat.forPattern("HH:mm").print(t.dateTime)
            s"${date}, ${time}, ${t.predicted},${t.actual}, ${t.delta}"
          }
            .mkString("\n")
        }
      .map(ts => Ok(ts))
  }

  def getStats = Action.async { implicit request =>
    computeSimplifiedResults.map { ts =>
      val dayDeltasAsArray = ts.filter(_.dateTime.isLessThan1DayOld).map(_.delta).toArray
      val weekDeltasAsArray = ts.filter(_.dateTime.isLessThan1WeekOld).map(_.delta).toArray
      val monthDeltasAsArray = ts.filter(_.dateTime.isLessThan1MonthOld).map(_.delta).toArray
      val allTimeDeltasAsArray = ts.map(_.delta).toArray

      val (meanDay, stdDay) = MeanStd.meanStd(dayDeltasAsArray)
      val (meanWeek, stdWeek) = MeanStd.meanStd(weekDeltasAsArray)
      val (meanMonth, stdMonth) = MeanStd.meanStd(monthDeltasAsArray)
      val (meanAllTime, stdAllTime) = MeanStd.meanStd(allTimeDeltasAsArray)


      val dayStats = SimpleStats(meanDay, stdDay, dayDeltasAsArray.length, "last day")
      val weekStats = SimpleStats(meanWeek, stdWeek, weekDeltasAsArray.length, "last week")
      val monthStats = SimpleStats(meanMonth, stdMonth, monthDeltasAsArray.length, "last Month")
      val allTimeStats = SimpleStats(meanAllTime, stdAllTime, allTimeDeltasAsArray.length, "all time")

      Ok(Json.toJson(Seq(dayStats, weekStats, monthStats, allTimeStats)))
    }
  }

  def rollingStats(days: Int = 1) = Action.async {implicit request =>
    computeSimplifiedResults.map{ts =>
      ts.groupBy(t => t.dateTime.getDayOfYear / days)
        .map{grouped =>
          val deltasAsArray = grouped._2.map(_.delta).toArray
          val (mean, std) = MeanStd.meanStd(deltasAsArray)
          val n = deltasAsArray.length
          SimpleStats(mean, std, n, s"${grouped._1} = dow mod $days")
        }
    }.map(ts => Ok(Json.toJson(ts)))
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
      .sortBy(p => -p.predictedTime.getMillis)
      .map(p => (p.predictedTime.explode, p))

    val keys = explodedPredictions.map(_._1).toSet

    parkingDataRepo.getAll.map(ds => {
      val crawledAndPreparedForPairing = ds.filter(_.hasUsefulData)
        .sortBy(-_.crawlingTime.getMillis)
        .map(d => (d.crawlingTime.explode, ParkingDataSetJson.from(d)))
        .filter(ed => keys.contains(ed._1))

      crawledAndPreparedForPairing
        .flatMap(c => findPartner(c, explodedPredictions))
        .sortBy(-_.prediction.predictedTime.getMillis)
    })
  }


  private def findPartner(dTuple: ((Int, Int, Int, Int, Int, Int), ParkingDataSetJson), explodedPredictions: List[((Int, Int, Int, Int, Int, Int), PredictionResult)])
  : Option[TmpEvalResult] = {
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
