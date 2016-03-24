import sbt._
import Process._
import Keys._
lazy val root = (project in file(".")).enablePlugins(JavaAppPackaging).enablePlugins(PlayScala).
    settings(
        name := "calculus",
        version := "1.0",
        scalaVersion := "2.11.6"
        //sbt version is 0.13.11
    )

libraryDependencies ++= {
    val akkaVersion = "2.3.9"
    val sprayVersion = "1.3.3"
    Seq(
    "io.spray" %% "spray-can" % sprayVersion,
    "io.spray" %% "spray-routing" % sprayVersion,
    "io.spray" %% "spray-json"    % "1.3.2",
    "io.spray" %% "spray-testkit" % sprayVersion,
    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "com.typesafe.akka" %% "akka-testkit" % akkaVersion,
    "com.typesafe.akka" %% "akka-slf4j" % akkaVersion
    )
}

resolvers ++= Seq(
    "Spray repository" at "http://repo.spray.io",
    "Akka Snapshot Repository" at "http://repo.akka.io/snapshots/",
    "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"
)

scalacOptions ++= Seq("-deprecation", "-feature")