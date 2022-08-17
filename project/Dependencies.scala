import sbt._

object Dependencies {
  val scalaTestVersion = "3.2.11"
  lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.2.11"
  val akkaVersion = "2.6.19"
  val pgSlickVersion = "0.20.2"
  val slickVersion = "3.3.3"
  val circleSlickVersion = "0.20.2"
  val scalaLoggingVersion = "3.9.4"

  lazy val akkaActor = "com.typesafe.akka" %% "akka-actor" % akkaVersion
  lazy val akkaStream = "com.typesafe.akka" %% "akka-stream" % akkaVersion
  lazy val pgSlick = "com.github.tminglei" %% "slick-pg" % pgSlickVersion
  lazy val slick = "com.typesafe.slick" %% "slick" % slickVersion
  lazy val tsSlick = "com.typesafe.slick" %% "slick-hikaricp" % slickVersion
  lazy val circeSlick = "com.github.tminglei" %% "slick-pg_circe-json" % circleSlickVersion

  val logs: Seq[ModuleID] = {
    val scalaLogging = "com.typesafe.scala-logging" %% "scala-logging" % scalaLoggingVersion
    List(scalaLogging)
  }
}
