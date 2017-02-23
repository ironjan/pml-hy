name := """de.ironjan.pppb"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  cache,
  filters,
  ws,
  "com.typesafe.play" %% "play-slick" % "2.0.0",
  "com.typesafe.play" %% "play-slick-evolutions" % "2.0.0",
  "com.typesafe.slick" %% "slick" % "3.1.1",
  "com.github.tototoshi" %% "slick-joda-mapper" % "2.2.0",
  "joda-time" % "joda-time" % "2.7",
  "org.joda" % "joda-convert" % "1.7",
  "org.postgresql" % "postgresql" % "9.4-1201-jdbc41",
  "net.ruippeixotog" %% "scala-scraper" % "1.2.0",
  "com.github.haifengl" % "smile-scala_2.11" % "1.2.2",
  "com.github.haifengl" % "smile-core" % "1.2.2",

  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test
)

libraryDependencies <+= scalaVersion("org.scala-lang" % "scala-compiler" % _)
