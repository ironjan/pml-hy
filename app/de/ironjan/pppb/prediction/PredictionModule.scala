package de.ironjan.pppb.prediction

import com.google.inject.AbstractModule
import de.ironjan.pppb.core.repository.ParkingDataRepository
import de.ironjan.pppb.prediction.repository.PredictionDataRepository

class PredictionModule extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[PredictionDataRepository]).toInstance(new PredictionDataRepository)
    bind(classOf[PredictionService]).asEagerSingleton()
 }
}
