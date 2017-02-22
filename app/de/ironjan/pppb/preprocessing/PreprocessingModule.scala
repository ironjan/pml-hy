package de.ironjan.pppb.preprocessing

import com.google.inject.AbstractModule
import de.ironjan.pppb.core.model.ParkingDataSet

class PreprocessingModule extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[Cleaner[ParkingDataSet]]).to(classOf[ParkingDataSetCleanerImpl])
  }
}
