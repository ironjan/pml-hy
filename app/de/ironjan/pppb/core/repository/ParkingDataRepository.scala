package de.ironjan.pppb.core.repository

import de.ironjan.pppb.core.BaseRepository
import de.ironjan.pppb.core.model.ParkingDataSet
import slick.lifted.TableQuery

class ParkingDataRepository extends BaseRepository[ParkingDataTable, ParkingDataSet](TableQuery[ParkingDataTable])
