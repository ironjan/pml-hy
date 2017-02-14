package repository
import com.reactore.repository.Tables.ParkingDataTable
import core.BaseRepository
import services.crawler.ParkingDataSet
import slick.lifted.TableQuery

class ParkingDataRepository extends BaseRepository[ParkingDataTable, ParkingDataSet](TableQuery[ParkingDataTable])
