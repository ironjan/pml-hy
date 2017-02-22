package de.ironjan.pppb.training

import com.google.inject.Inject
import de.ironjan.pppb.core.model.ParkingDataSet
import de.ironjan.pppb.core.repository.ParkingDataRepository
import org.joda.time.DateTime
import play.api.Logger

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
    val boundary =  (ds.length*3/4 + 1).toInt
    val splitSet = (ds.slice(0, boundary), ds.slice(boundary, ds.length-1))
    trainSubset(ts, splitSet, splitSet._1.head.capacity.get) // TODO just using get on option

    val totalTrainingTime = DateTime.now().getMillis - ts.getMillis
    Logger.debug(s"Total training time for all subsets: ${totalTrainingTime}ms.")
  }

  private def trainSubset(ts: DateTime, ds: (Seq[ParkingDataSet], Seq[ParkingDataSet]), capacity: Int) = {
    val trainingSet = ds._1
    val testSet = ds._2
    Logger.debug(s"Training subset of length ${trainingSet.length} and test set of length ${testSet.length} (Set of $ts)  ")

    val unzipped = unzipSet(trainingSet)

    // TODO better way for double .toArray?
    val x = unzipped._1.toArray
    val y = unzipped._2.toArray


    Logger.debug(s"Prepared training data.")

    regressionTree(testSet, x, y)
  }

  private def regressionTree(testSet: Seq[ParkingDataSet], x: Array[Array[Double]], y: Array[Double]) = {
    val beforeTraining = System.currentTimeMillis

    val regressionTree = smile.regression.cart(x, y, maxNodes = 100)


    val trainingTime = System.currentTimeMillis - beforeTraining
    Logger.debug(s"Got regression tree in ${trainingTime}ms: maxDepth=${regressionTree.maxDepth()}.")

    val unzipSet1 = unzipSet(testSet)
    val aes = unzipSet1._1.map(regressionTree.predict)
      .zip(unzipSet1._2)
      .map(p => {
        val yStar = p._1
        val y = p._2

        Math.abs(yStar - y)
      })
    val mae = aes.sum / aes.length
    Logger.debug(s"regression tree had a mean average error of $mae.")
  }

  private def unzipSet(trainingSet: Seq[ParkingDataSet]) = {
    trainingSet.filter(_.hasUsefulData)
      .map(_.toMlTrainingTuple)
      .unzip
  }

}