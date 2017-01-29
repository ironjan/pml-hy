package com.reactore.repository

import java.sql.Date

import core.{BaseEntity, BaseTable}
import services.crawler.RawParkingDataSet
import slick.driver.PostgresDriver
import com.github.tototoshi.slick.PostgresJodaSupport._


object Tables extends {
  val profile = PostgresDriver
} with Tables

trait Tables {
  val profile: PostgresDriver

  import profile.api._

  class RawParkingDataTable(_tableTag: Tag) extends BaseTable[RawParkingDataSet](_tableTag, Some("public"), "raw_parking_data") {
    def * = (dateTime, name, inUse, capacity, city, id, isDeleted) <>(RawParkingDataSet.tupled, RawParkingDataSet.unapply)

    def ? = (Rep.Some(dateTime), Rep.Some(name), Rep.Some(inUse), Rep.Some(capacity), Rep.Some(city), Rep.Some(id), Rep.Some(isDeleted)).shaped.<>({ r => import r._; _1.map(_ => RawParkingDataSet.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get, _7.get))) }, (_: Any) => throw new Exception("Inserting into ? projection not supported."))

    override val id: Rep[Long] = column[Long]("id", O.AutoInc, O.PrimaryKey)
    val name: Rep[String] = column[String]("name", O.Length(255, varying = true))
    val inUse: Rep[String] = column[String]("used")
    val capacity: Rep[String] = column[String]("capacity")
    val city: Rep[String] = column[String]("city")
    val dateTime: Rep[org.joda.time.DateTime] = column[org.joda.time.DateTime]("crawling_time")
    override val isDeleted: Rep[Boolean] = column[Boolean]("is_deleted", O.Default(false))
  }

  lazy val rawParkingDataTable = new TableQuery(tag => new RawParkingDataTable(tag))

}