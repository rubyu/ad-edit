
organization := "com.github.rubyu"

name := "ad-update"

version := "0.0.0"

scalaVersion := "2.9.2"

scalacOptions := Seq("-unchecked", "-deprecation")

libraryDependencies ++= Seq(
    "org.slf4j" % "slf4j-api" % "1.6.4",
    "org.slf4j" % "slf4j-simple" % "1.6.4",
    "args4j" % "args4j" % "2.0.26",
    "com.orangesignal" % "orangesignal-csv" % "2.1.0",
    "org.fusesource.scalate" % "scalate-core" % "1.5.3",
    "org.specs2" %% "specs2" % "1.12.2" % "test",
    "junit" % "junit" % "4.7" % "test"
  )
