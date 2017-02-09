import java.time.Clock

import com.google.inject.AbstractModule
import repository.ParkingDataRepository
import services.cleaner.{Cleaner, CleanerService, ParkingDataSetCleanerImpl}
import services.crawler.{PaderbornCrawler, PaderbornCrawlerImpl, ParkingDataSet, ParkingDataSetCleaner}
import services.{ApplicationTimer, CrawlingService, DBCleaningService}
import services.dbcleaner.{DBCleaner, DBCleanerTrait}
/**
 * This class is a Guice module that tells Guice how to bind several
 * different types. This Guice module is created when the Play
 * application starts.

 * Play will automatically use any class called `Module` that is in
 * the root package. You can create modules in other locations by
 * adding `play.modules.enabled` settings to the `application.conf`
 * configuration file.
 */
class Module extends AbstractModule {

  override def configure() = {
    // Use the system clock as the default implementation of Clock
    bind(classOf[Clock]).toInstance(Clock.systemDefaultZone)
    // Ask Guice to create an instance of ApplicationTimer when the
    // application starts.
    bind(classOf[ApplicationTimer]).asEagerSingleton()
    bind(classOf[CrawlingService]).asEagerSingleton()
    bind(classOf[CleanerService]).asEagerSingleton()
    bind(classOf[DBCleaningService]).asEagerSingleton()

    bind(classOf[ParkingDataRepository]).toInstance(new ParkingDataRepository)
    bind(classOf[PaderbornCrawler]).to(classOf[PaderbornCrawlerImpl])
    bind(classOf[ParkingDataSetCleaner]).to(classOf[ParkingDataSetCleanerImpl])
    bind(classOf[DBCleanerTrait]).to(classOf[DBCleaner])
  }

}
