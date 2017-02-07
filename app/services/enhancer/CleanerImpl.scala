package services.enhancer

import services.crawler.ParkingDataSet

/**
  * Implementation of Cleaner for ParkingDataSet
  */
class CleanerImpl extends Cleaner[ParkingDataSet] {
  override def cleanDatabase: Unit = ???

  override def cleanEntry(t: ParkingDataSet): ParkingDataSet = {

  }
}
