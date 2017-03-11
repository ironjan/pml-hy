package de.ironjan.pppb.evaluation

import play.api.libs.json.Json

case class SimpleStats(mean: Double,
                       std: Double,
                       n: Int,
                       interval: String)

object SimpleStats{
  implicit val simpleStatsWrite = Json.writes[SimpleStats]
}
