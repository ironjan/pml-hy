package services.enhancer

/**
  * This service will clean the data
  */
trait Cleaner[T] {
  def cleanDatabase: Unit

  def cleanEntry(t: T): T
}
