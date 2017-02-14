package de.ironjan.pppb.core.repository

import slick.driver.PostgresDriver
import slick.lifted.TableQuery


object SlickTables extends {
  val profile = PostgresDriver
} with SlickTables

trait SlickTables {
  val profile: PostgresDriver

  lazy val rawParkingDataTable = new TableQuery(tag => new ParkingDataTable(tag))

}