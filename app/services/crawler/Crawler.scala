package services.crawler


import java.time.chrono.Chronology

import scala.io.Source
import scala.xml.XML
import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL.Parse._
import net.ruippeixotog.scalascraper.model.{Document, Element}
import org.joda.time.{DateTime, DateTimeZone}
import play.api.Logger

import scala.language.postfixOps
import scala.reflect.macros.whitebox

/**
  * Actual Crawler that is triggered by [[services.CrawlingService]]
  */
object Crawler {
  val Url = "https://www4.paderborn.de/ParkInfoASP/default.aspx"


  def crawl = {
    val crawlingTime = DateTime.now()
    extractData(downloadDocument)
      .flatMap(convertToRawParkingDataSet(crawlingTime, _))
      .foreach(println)
  }

  private def convertToRawParkingDataSet(crawlingTime: DateTime, x: Array[String]) = {
    val name = x(0)
    val currentUsage = x(3)
    val maxCapacity = x(2)
    try {
      Some(RawParkingDataSet(crawlingTime, name, currentUsage, maxCapacity))
    } catch {
      case e: NumberFormatException => {
        Logger.warn(s"NumberFormatException for RawParkingDataSet($crawlingTime, $name, $currentUsage, $maxCapacity")
        None
      }
    }
  }

  private def extractData(doc: Document): List[Array[String]] = {
    val tableRows: List[Element] = doc >> elementList("tr")

    // only keep actual data lines
    val result = tableRows.map(_ >> texts("td"))
      .map(_ toArray)
      .tail
    result
  }

  private def downloadDocument: Document = {
    val browser = JsoupBrowser()
    val doc = browser.get(Url)
    doc
  }

}
