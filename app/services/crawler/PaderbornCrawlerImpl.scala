package services.crawler

import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.model.{Document, Element}
import org.joda.time.DateTime
import repository.RawParkingDataRepository

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.language.postfixOps

trait PaderbornCrawler extends Crawler{
  override def city = Cities.Paderborn
}

class PaderbornCrawlerImpl extends PaderbornCrawler{
  val Url = "https://www4.paderborn.de/ParkInfoASP/default.aspx"


  override def crawl = {
    val crawlingTime = DateTime.now()
    val repo = new RawParkingDataRepository
    extractData(downloadDocument)
      .flatMap(convertToRawParkingDataSet(crawlingTime, _))
      .foreach(d => Await.result(repo.save(d), 5 seconds))
  }

  private def convertToRawParkingDataSet(crawlingTime: DateTime, x: Array[String]) = {
    val location = x(0)
    val used = x(3)
    val capacity = x(2)

    Some(ParkingDataSet(crawlingTime, location, used, capacity, Cities.Paderborn))
  }

  private def extractData(doc: Document): List[Array[String]] = {
    val tableRows: List[Element] = doc >> elementList("tr")

    // only keep actual data lines
    val result = tableRows.map(_ >> texts("td"))
      .map(_ toArray)
      .tail
    result
  }

  private def downloadDocument: Document = JsoupBrowser().get(Url)

}
