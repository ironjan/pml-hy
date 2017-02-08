package services.dbcleaner

import com.google.inject.Inject

import repository.ParkingDataRepository

class DBCleaner  @Inject()(repo: ParkingDataRepository){
  def removeUnneededEntries: Unit = {
    repo.getAll
    .map(_.filter(_.isDeleteable).map(doSomething).foreach(d => Await.result(parkingDataRepository.deleteById(d.id), 5 seconds)))
  }
}