
import AssemblyKeys._

organization := "com.github.rubyu"

name := "ad-edit"

version := "0.0.1"

scalaVersion := "2.10.4"

libraryDependencies ++= Seq(
    "org.slf4j" % "slf4j-api" % "1.6.4",
    "org.slf4j" % "slf4j-simple" % "1.6.4",
    "args4j" % "args4j" % "2.0.26",
    "org.fusesource.scalate" % "scalate-core_2.10" % "1.6.1",
    "org.specs2" % "specs2_2.10" % "2.3.12" % "test",
    "junit" % "junit" % "4.7" % "test"
  )

logBuffered in Test := false

parallelExecution in Test := false

mainClass in assembly := Some("com.github.rubyu.adedit.Main")

jarName in assembly <<= (name, version) { (name, version) => name + "-" + version + ".jar" }

assemblySettings

mergeStrategy in assembly <<= (mergeStrategy in assembly) { (old) =>
  {
    case "rootdoc.txt" => MergeStrategy.concat
    case x => old(x)
  }
}