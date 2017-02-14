package services.crawler

import javax.inject.Inject

import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.model.{Document, Element}
import org.joda.time.DateTime
import repository.ParkingDataRepository
import services.cleaner.ParkingDataSetCleanerImpl

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.language.postfixOps

trait PaderbornCrawler extends Crawler {
  override def city = Cities.Paderborn
}

class PaderbornCrawlerImpl @Inject()(cleaner: ParkingDataSetCleanerImpl)
  extends PaderbornCrawler {
  val Url = "https://www4.paderborn.de/ParkInfoASP/default.aspx"

  val LiboriGaleriePrefix = "P6"

  override def crawl = {
    val crawlingTime = DateTime.now()
    val repo = new ParkingDataRepository

    val crawledEntries = extractData(downloadDocument)
      .flatMap(convertToRawParkingDataSet(crawlingTime, _))
      .filter(_.name.startsWith(LiboriGaleriePrefix))
      .map(cleaner.cleanEntry)
      .map(d => Await.result(repo.save(d), 5 seconds))

    val count = crawledEntries.count(_ => true)
    val totalCount = Await.result(repo.getAll.map(_.count(_ => true)), 10 seconds)
    println(s"Crawled $count new entries. Total: $totalCount")
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
