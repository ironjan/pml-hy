package de.ironjan.pppb.training

import com.google.inject.Inject
import de.ironjan.pppb.core.model.ParkingDataSet
import de.ironjan.pppb.core.repository.ParkingDataRepository
import org.joda.time.DateTime
import play.api.Logger
import smile.regression.RegressionTree

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by Jan Lippert on 19.02.2017.
  */
class Trainer @Inject()(parkingDataRepository: ParkingDataRepository) {
  def doSomething = {
    Logger.debug(s"Started training.")

    val ds = Await.result(parkingDataRepository.getAll, Duration.Inf)

    val ts = DateTime.now()

    // Append checking set & predict it 
    ds.zipWithIndex
      .map(t => ds.slice(0, t._2 + 1))
      .foreach(ds => trainSubset(ts, ds))

    val totalTrainingTime = DateTime.now().getMillis - ts.getMillis
    Logger.debug(s"Total training time for all subsets: ${totalTrainingTime}ms.")
  }

  private def trainSubset(ts: DateTime, ds: Seq[ParkingDataSet]) = {
    Logger.debug(s"Training subset of length ${ds.length} (Set of $ts)  ")

    val unzipped: (Seq[Array[Double]], Seq[Double]) =
      ds.filter(_.hasUsefulData)
        .map(_.toMlTrainingTuple)
        .unzip

    // TODO better way for double .toArray?
    val x = unzipped._1.toArray
    val y = unzipped._2.toArray


    val beforeTraining = System.currentTimeMillis
    Logger.debug(s"Prepared training data.")

    val regressionTree = smile.regression.cart(x, y, maxNodes = 100)

    val trainingTime = System.currentTimeMillis - beforeTraining
    Logger.debug(s"Got regression tree in ${trainingTime}ms.")
    Logger.debug(s"Results: ${regressionTree.importance()} ${regressionTree.maxDepth()}.")
  }

}