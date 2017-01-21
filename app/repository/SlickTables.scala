import org.joda.time.DateTime
import slick.lifted.{Rep, Tag}
import com.github.tototoshi.slick.PostgresJodaSupport._

case class RawCrawl(id: Long,
                    datetime: DateTime,
                    rawContent: String,
                    isDeleted: Boolean)
  extends BaseEntity

trait Tables {

  class RawCrawlTable(_tableTag: Tag) extends BaseTable[RawCrawl](_tableTag, Some("raw_crawls"), "RawCrawl") {

    def * = (id, dateTime, rawContent, isDeleted) <> (RawCrawl.tupled, RawCrawl.unapply)

    def ? = (Rep.Some(id), Rep.Some(dateTime), Rep.Some(rawContent), Rep.Some(isDeleted)).shaped.<>({ r =>
      import r._; _1.map(_ => RawCrawl.tupled((_1.get, _2.get, _3.get, _4.get)))
    },latex
      (_: Any) => throw new Exception("Inserting into ? projection not supported."))

    override val id: Rep[Long] = column[Long]("_id", O.AutoInc, O.PrimaryKey)
    val dateTime: Rep[DateTime] = column[DateTime]("datetime")
    val rawContent: Rep[String] = column[String]("raw_content")
    override val isDeleted: Rep[Boolean] = column[Boolean]("is_deleted", O.Default(false))


  }

}