package de.ironjan.pppb.crawling

import com.google.inject.AbstractModule

class CrawlingModule extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[PaderbornCrawler]).to(classOf[PaderbornCrawlerImpl])
    bind(classOf[CrawlingService]).asEagerSingleton()
  }
}
