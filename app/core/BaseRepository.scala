package core

import slick.lifted.{CanBeQueryCondition, Rep, TableQuery}

import scala.concurrent.Future
import scala.reflect._
import slick.backend.DatabaseConfig
import slick.driver.PostgresDriver
import PostgresDriver.api._


object DriverHelper {
  val dbConfig: DatabaseConfig[PostgresDriver] = DatabaseConfig.forConfig("slick.dbs.default")
  val db = dbConfig.db
}


/**
  * Provides CRUD operations for the tables.
  * @tparam T sub type of [[BaseTable]]
  * @tparam E sub type of [[BaseEntity]]
  * @see [[http://reactore.com/repository-patterngeneric-dao-implementation-in-scala-using-slick-3/]]
  */
trait BaseRepositoryComponent[T <: BaseTable[E], E <: BaseEntity] {

  /**
    * Gets the record with the primary key id provided
    * @param id the id to search for
    * @return the found record or [[None]]
    */
  def getById(id: Long) : Future[Option[E]]

  /**
    * Gets all the records from the table
    * @return a [[Seq]] containing all records
    */
  def getAll : Future[Seq[E]]

  /**
    * Filter the table records with provided conditions
    * @param expr
    * @param wt
    * @tparam C
    * @return a [[Seq]] containing the records that fulfilled the conditions
    *  @todo Add documentation for expr, wt, C
    */
  def filter[C <: Rep[_]](expr: T => C)(implicit wt: CanBeQueryCondition[C]): Future[Seq[E]]

  /**
    * Insert a record to the table
    * @param row the new record
    * @return the record after saving
    */
  def save(row: E) : Future[E]

  /**
    * Delete a record by its primary key
    * @param id the id of the record that is to be deleted
    * @return the number of deleted rows
    *         @todo Verify @return
    */
  def deleteById(id: Long) : Future[Int]

  /**
    * Update an existing record
    * @param id the id of the record
    * @param row the new values
    * @return the record after saving
    */
  def updateById(id: Long, row: E) : Future[Int]
}

trait BaseRepositoryQuery[T <: BaseTable[E], E <: BaseEntity] {

  val query: PostgresDriver.api.type#TableQuery[T]

  def getByIdQuery(id: Long) = {
    query.filter(_.id === id).filter(_.isDeleted === false)
  }

  def getAllQuery = {
    query.filter(_.isDeleted === false)
  }

  def filterQuery[C <: Rep[_]](expr: T => C)(implicit wt: CanBeQueryCondition[C]) = {
    query.filter(expr).filter(_.isDeleted === false)
  }

  def saveQuery(row: E) = {
    query returning query += row
  }

  def deleteByIdQuery(id: Long) = {
    query.filter(_.id === id).map(_.isDeleted).update(true)
  }

  def updateByIdQuery(id: Long, row: E) = {
    query.filter(_.id === id).filter(_.isDeleted === false).update(row)
  }

}


abstract class BaseRepository[T <: BaseTable[E], E <: BaseEntity : ClassTag](clazz: TableQuery[T]) extends BaseRepositoryQuery[T, E] with BaseRepositoryComponent[T,E] {

  val clazzTable: TableQuery[T] = clazz
  lazy val clazzEntity = classTag[E].runtimeClass
  val query: PostgresDriver.api.type#TableQuery[T] = clazz
  val db = DriverHelper.db

  def getAll: Future[Seq[E]] = {
    db.run(getAllQuery.result)
  }

  def getById(id: Long): Future[Option[E]] = {
    db.run(getByIdQuery(id).result.headOption)
  }

  def filter[C <: Rep[_]](expr: T => C)(implicit wt: CanBeQueryCondition[C]) = {
    db.run(filterQuery(expr).result)
  }

  def save(row: E) = {
    db.run(saveQuery(row))
  }

  def updateById(id: Long, row: E) = {
    db.run(updateByIdQuery(id, row))
  }

  def deleteById(id: Long) = {
    db.run(deleteByIdQuery(id))
  }

}