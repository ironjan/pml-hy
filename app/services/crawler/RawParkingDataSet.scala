package services.crawler

import core._

import org.joda.time.DateTime

/**
  */
case class RawParkingDataSet(dateTime: DateTime,
                             name: String,
                             inUSe: String,
                             capacity: String,
                             id: Long = -1,
                             isDeleted: Boolean = false)
  extends BaseEntity
