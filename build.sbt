import Dependencies._

ThisBuild / scalaVersion     := "2.13.8"
ThisBuild / organization     := "akka-examples"
ThisBuild / organizationName := "examples"

lazy val root = (project in file("."))
  .settings(
    name := ".",
    libraryDependencies += scalaTest % Test,
    libraryDependencies += slick,
    libraryDependencies += akkaStream,
    libraryDependencies += tsSlick,
    libraryDependencies += pgSlick,
    libraryDependencies += circeSlick,
    libraryDependencies ++= logs
  )
