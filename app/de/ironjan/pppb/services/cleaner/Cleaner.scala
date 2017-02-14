package de.ironjan.pppb.services.cleaner

/**
  * This service will clean the data
  */
trait Cleaner[T] {
  def cleanDatabase: Unit

  def cleanEntry(t: T): T
}
