name := """move-your-body"""
organization := "com.moveyourbody"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.1"

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test
libraryDependencies += "com.typesafe.play" %% "play-slick" % "5.0.0"
libraryDependencies += "com.h2database" % "h2" % "1.4.199"
libraryDependencies += "com.typesafe.play" %% "play-slick-evolutions" % "5.0.0"
// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.moveyourbody.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.moveyourbody.binders._"
