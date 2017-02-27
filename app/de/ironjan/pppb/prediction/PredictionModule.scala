package de.ironjan.pppb.prediction

import com.google.inject.AbstractModule

class PredictionModule extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[PredictionService]).asEagerSingleton()
 }
}
