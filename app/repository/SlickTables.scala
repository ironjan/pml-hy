package com.reactore.repository

import com.github.tototoshi.slick.PostgresJodaSupport._
import core.BaseTable
import services.crawler.ParkingDataSet
import slick.driver.PostgresDriver


object Tables extends {
  val profile = PostgresDriver
} with Tables

trait Tables {
  val profile: PostgresDriver

  import profile.api._

  class ParkingDataTable(_tableTag: Tag) extends BaseTable[ParkingDataSet](_tableTag, Some("public"), "parking_data") {
    def * = (dateTime, name, free, capacity, city, id, isDeleted,
      modelVersion, hourOfDay, minuteOfHour, dayOfWeek, dayOfMonth, weekOfMonth, weekOfYear) <> (ParkingDataSet.tupled, ParkingDataSet.unapply)

    def ? = (Rep.Some(dateTime), Rep.Some(name), Rep.Some(free), Rep.Some(capacity), Rep.Some(city), Rep.Some(id),
      Rep.Some(isDeleted),
      Rep.Some(modelVersion), Rep.Some(hourOfDay), Rep.Some(minuteOfHour), Rep.Some(dayOfWeek), Rep.Some(dayOfMonth),
      Rep.Some(weekOfMonth), Rep.Some(weekOfYear)).shaped.<>({ r =>
      import r._;
      _1.map(_ => ParkingDataSet.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get,
        _7.get, _8.get, _9.get, _10.get, _11.get, _12.get, _13.get, _14.get)))
    }, (_: Any) => throw new Exception("Inserting into ? projection not supported."))

    override val id: Rep[Long] = column[Long]("id", O.AutoInc, O.PrimaryKey)
    val name: Rep[String] = column[String]("name", O.Length(255, varying = true))
    val free: Rep[String] = column[String]("free")
    val capacity: Rep[String] = column[String]("capacity")
    val city: Rep[String] = column[String]("city")
    val dateTime: Rep[org.joda.time.DateTime] = column[org.joda.time.DateTime]("crawling_time")
    override val isDeleted: Rep[Boolean] = column[Boolean]("is_deleted", O.Default(false))

    val modelVersion: Rep[Option[Int]] = column[Option[Int]]("model_version")
    val hourOfDay: Rep[Option[Int]] = column[Option[Int]]("hour_of_day")
    val minuteOfHour: Rep[Option[Int]] = column[Option[Int]]("minute_of_hour")
    val dayOfWeek: Rep[Option[Int]] = column[Option[Int]]("day_of_week")
    val dayOfMonth: Rep[Option[Int]] = column[Option[Int]]("day_of_month")
    val weekOfMonth: Rep[Option[Int]] = column[Option[Int]]("week_of_month")
    val weekOfYear: Rep[Option[Int]] = column[Option[Int]]("week_of_year")
  }

  lazy val rawParkingDataTable = new TableQuery(tag => new ParkingDataTable(tag))

}