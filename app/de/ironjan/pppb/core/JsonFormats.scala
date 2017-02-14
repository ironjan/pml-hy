package services.crawler

import core._
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import play.api.libs.json._

/** 
  * DateFormatter for Json based on http://stackoverflow.com/a/31496260 
  */
object JsonFormats {
  val dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSSZ"

  implicit val jodaDateReads = Reads[DateTime](js =>
    js.validate[String].map[DateTime](dtString =>
      DateTime.parse(dtString, DateTimeFormat.forPattern(dateFormat))
    )
  )

  implicit val jodaDateWrites: Writes[DateTime] = new Writes[DateTime] {
    def writes(d: DateTime): JsValue = JsString(d.toString())
  }

}