import sbt._
import Process._
import Keys._
lazy val root = (project in file(".")).
    settings(
        name := "calculator",
        version := "1.0",
        scalaVersion := "2.11.8"
    )

libraryDependencies ++= Seq(
    "io.spray" % "spray-can" % "1.1-M8",
    "io.spray" % "spray-http" % "1.1-M8",
    "io.spray" % "spray-routing" % "1.1-M8"
)

resolvers ++= Seq(
    "Spray repository" at "http://repo.spray.io",
    "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"
)