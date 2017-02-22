package de.ironjan.pppb.crawling

trait Crawler {
  def city: String

  def crawl: Unit
}
