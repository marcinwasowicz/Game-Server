name := "GameServerProject"

version := "0.1"

scalaVersion := "2.13.1"

libraryDependencies +=
  "com.typesafe.akka" %% "akka-actor" % "2.6.5"
libraryDependencies +=
  "com.typesafe.akka" %% "akka-remote" % "2.6.5"
libraryDependencies +=
  "com.typesafe.akka" %% "akka-cluster" % "2.6.5"
libraryDependencies +=
  "com.typesafe.akka" %% "akka-serialization-jackson" % "2.6.5"
