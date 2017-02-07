package repository
import com.reactore.repository.Tables.ParkingDataTable
import core.{BaseRepository, BaseRepositoryQuery}
import services.crawler.ParkingDataSet
import slick.lifted.TableQuery

class RawParkingDataRepository extends BaseRepository[ParkingDataTable, ParkingDataSet](TableQuery[ParkingDataTable])
