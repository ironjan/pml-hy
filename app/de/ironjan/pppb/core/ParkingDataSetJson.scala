package de.ironjan.pppb.core

import de.ironjan.pppb.core.model.ParkingDataSet
import org.joda.time.DateTime
import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Writes}

case class ParkingDataSetJson(crawlingTime: DateTime,
                              name: String,
                              city: String,
                              free: Option[Int] = None,
                              capacity: Option[Int] = None) {

}

object ParkingDataSetJson {
  implicit val parkingDataSetJsonWrites: Writes[ParkingDataSetJson] = (
    (JsPath \ "crawlingTime").write[DateTime](JsonFormats.jodaDateWrites) and
      (JsPath \ "name").write[String] and
      (JsPath \ "city").write[String] and
      (JsPath \ "free").write[Option[Int]] and
      (JsPath \ "capacity").write[Option[Int]]
    ) (unlift(ParkingDataSetJson.unapply))

  def tupled = (ParkingDataSetJson.apply _).tupled

  def from(p: ParkingDataSet): ParkingDataSetJson =
    ParkingDataSetJson(p.crawlingTime, p.name, p.city, p.free, p.capacity)
}