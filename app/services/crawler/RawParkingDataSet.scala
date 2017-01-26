package services.crawler

import org.joda.time.DateTime

/**
  */
case class RawParkingDataSet(dateTime: DateTime,
                             name: String,
                             inUSe: Int,
                             capacity: Int)

object RawParkingDataSet{
  def apply(dateTime: DateTime,
            name: String,
            inUSe: String,
            capacity: String): RawParkingDataSet = new RawParkingDataSet(dateTime, name, inUSe.toInt, capacity.toInt)
}