package de.ironjan.pppb.repository

import com.github.tototoshi.slick.PostgresJodaSupport._
import de.ironjan.pppb.core.BaseTable
import de.ironjan.pppb.services.crawler.ParkingDataSet
import slick.driver.PostgresDriver
import slick.lifted.TableQuery


object SlickTables extends {
  val profile = PostgresDriver
} with SlickTables

trait SlickTables {
  val profile: PostgresDriver

  lazy val rawParkingDataTable = new TableQuery(tag => new ParkingDataTable(tag))

}