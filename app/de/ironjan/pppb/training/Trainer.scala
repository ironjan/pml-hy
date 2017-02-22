package de.ironjan.pppb.training

import com.google.inject.Inject
import de.ironjan.pppb.core.model.ParkingDataSet
import de.ironjan.pppb.core.repository.ParkingDataRepository
import org.joda.time.DateTime
import play.api.Logger
import smile.regression.Regression

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

    evaluate(RegressionTreeTraining.train(x, y), testSet)
    evaluate(RandomForestClassificator.train(x, y), testSet)
  }

  private def evaluate(regression: Regression[Array[Double]], T: Seq[ParkingDataSet]) ={
    val xStars = unzipSet(T)._1
    val yStars = unzipSet(T)._2

    val aes = xStars.map(regression.predict)
      .zip(yStars)
      .map(p => Math.abs(p._1 - p._2))

    val mae = aes.sum / aes.length
    Logger.debug(s"$regression had a mean average error of $mae.")

  }

  private def unzipSet(trainingSet: Seq[ParkingDataSet]) = {
    trainingSet.filter(_.hasUsefulData)
      .map(_.toMlTrainingTuple)
      .unzip
  }

}