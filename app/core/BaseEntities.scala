package core

import slick.driver.PostgresDriver.api._

import scala.reflect._

/**
  * First of all, we need to create a generic trait and Slick abstract class which every other table entity needs to implement.
  * @see [[http://reactore.com/repository-patterngeneric-dao-implementation-in-scala-using-slick-3/]]
  */
trait BaseEntity {
  val id: Long
  val isDeleted: Boolean
}

/**
  * First of all, we need to create a generic trait and Slick abstract class which every other table entity needs to implement.
  * @see [[http://reactore.com/repository-patterngeneric-dao-implementation-in-scala-using-slick-3/]]
  */
abstract class BaseTable[E: ClassTag](tag: Tag, schemaName: Option[String], tableName: String)
  extends Table[E](tag, schemaName, tableName) {
  val classOfEntity = classTag[E].runtimeClass
  val id: Rep[Long] = column[Long]("Id", O.PrimaryKey, O.AutoInc)
  val isDeleted: Rep[Boolean] = column[Boolean]("IsDeleted", O.Default(false))
}
