package services.crawler


import java.time.chrono.Chronology

import scala.io.Source
import scala.xml.XML
import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL.Parse._
import net.ruippeixotog.scalascraper.model.Element
import org.joda.time.{DateTime, DateTimeZone}

/**
  * Actual Crawler that is triggered by [[services.CrawlingService]]
  */
object Crawler {
  val Url = "https://www4.paderborn.de/ParkInfoASP/default.aspx"


  def crawl  = {
    val crawlingTime = DateTime.now()
    val browser = JsoupBrowser()
    val doc  = browser.get(Url)
    val tableRows:  List[Element] = doc  >> elementList("tr")

    tableRows.map(_ >> texts("td"))
        .tail
        .map(_.filterNot(_.isEmpty))
      .foreach(println)
  }
}
