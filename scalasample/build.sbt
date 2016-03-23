import sbt._
import Process._
import Keys._
lazy val root = (project in file(".")).
    settings(
        name := "calculator",
        version := "1.0",
        scalaVersion := "2.11.1"
        //sbt version is 0.13.11
    )

libraryDependencies ++= {
    val akkaVersion = "2.3.2"
    val sprayVersion = "1.3.1"
    Seq(
    "io.spray" % "spray-can" % sprayVersion,
    "io.spray" % "spray-http" % sprayVersion,
    "io.spray" % "spray-routing" % sprayVersion,
    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "com.typesafe.akka" %% "akka-slf4j" % akkaVersion
    )
}

resolvers ++= Seq(
    "Spray repository" at "http://repo.spray.io",
    "Akka Snapshot Repository" at "http://repo.akka.io/snapshots/",
    "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"
)

scalacOptions += "-deprecation"