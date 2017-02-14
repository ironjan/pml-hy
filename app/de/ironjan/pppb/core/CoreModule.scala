package de.ironjan.pppb.core

import com.google.inject.AbstractModule
import de.ironjan.pppb.core.repository.ParkingDataRepository

class CoreModule extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[ParkingDataRepository]).toInstance(new ParkingDataRepository)
  }
}
