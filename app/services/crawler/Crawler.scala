package services.crawler

trait Crawler {
  def city: String

  def crawl: Unit
}
