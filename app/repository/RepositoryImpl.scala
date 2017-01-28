package repository
import com.reactore.repository.Tables.RawParkingDataTable
import core.BaseRepository
import services.crawler.RawParkingDataSet
import slick.lifted.TableQuery

class RawParkingDataRepository extends BaseRepository[RawParkingDataTable, RawParkingDataSet](TableQuery[RawParkingDataTable])
