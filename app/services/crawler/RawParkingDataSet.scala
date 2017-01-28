package services.crawler

import core._

import org.joda.time.DateTime

/**
  */
case class RawParkingDataSet(dateTime: DateTime,
                             name: String,
                             inUSe: Int,
                             capacity: Int,
                             id: Long = -1,
                             isDeleted: Boolean = false)
  extends BaseEntity

object RawParkingDataSet {
  def fromRaw(dateTime: DateTime,
            name: String,
            inUSe: String,
            capacity: String): RawParkingDataSet = new RawParkingDataSet(dateTime, name, inUSe.toInt, capacity.toInt)
def tupled = (RawParkingDataSet.apply _).tupled
}