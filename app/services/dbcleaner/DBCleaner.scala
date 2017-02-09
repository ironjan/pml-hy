package services.dbcleaner

import com.google.inject.Inject

import repository.ParkingDataRepository
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Await
import scala.concurrent.duration._


class DBCleaner @Inject()(repo: ParkingDataRepository)
  extends DBCleanerTrait{
  def cleanDb: Unit = {
    repo.getAll
    .map(_.filter(_.isDeleteable).foreach(d => Await.result(repo.deleteById(d.id), 5 seconds)))
  }
}