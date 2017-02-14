package de.ironjan.pppb

import java.time.Clock

import com.google.inject.AbstractModule
import de.ironjan.pppb.core.model.ParkingDataSet
import de.ironjan.pppb.core.repository.ParkingDataRepository
import de.ironjan.pppb.crawling.{CrawlingService, PaderbornCrawler, PaderbornCrawlerImpl}
import de.ironjan.pppb.preprocessing.{Cleaner, CleanerService, ParkingDataSetCleanerImpl}

/**
 * This class is a Guice module that tells Guice how to bind several
 * different types. This Guice module is created when the Play
 * application starts.

 * Play will automatically use any class called `de.ironjan.pppb.Module` that is in
 * the root package. You can create modules in other locations by
 * adding `play.modules.enabled` settings to the `application.conf`
 * configuration file.
 */
class Module extends AbstractModule {

  override def configure() = {
    // Use the system clock as the default implementation of Clock
    bind(classOf[Clock]).toInstance(Clock.systemUTC())
    bind(classOf[CrawlingService]).asEagerSingleton()
    bind(classOf[CleanerService]).asEagerSingleton()

    bind(classOf[ParkingDataRepository]).toInstance(new ParkingDataRepository)
    bind(classOf[PaderbornCrawler]).to(classOf[PaderbornCrawlerImpl])
    bind(classOf[Cleaner[ParkingDataSet]]).to(classOf[ParkingDataSetCleanerImpl])
  }

}
