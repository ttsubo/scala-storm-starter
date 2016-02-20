import AssemblyKeys._

seq(assemblySettings: _*)

name := "scala-storm-starter"

version := "0.0.2-SNAPSHOT"

scalaVersion := "2.11.7"

fork in run := true

resolvers ++= Seq(
  "twitter4j" at "http://twitter4j.org/maven2",
  "clojars.org" at "http://clojars.org/repo"
)

libraryDependencies ++= Seq(
  "org.apache.kafka" % "kafka_2.11" % "0.8.2.1"
      exclude("org.slf4j", "slf4j-api")
      exclude("org.slf4j", "slf4j-log4j12"),
  "org.apache.storm" % "storm-core" % "0.10.0" % "provided"
      exclude("org.slf4j", "slf4j-simple")
      exclude("org.slf4j", "slf4j-api")
      exclude("org.slf4j", "slf4j-log4j12"),
  "org.apache.storm" % "storm-kafka" % "0.10.0"
      exclude("org.slf4j", "slf4j-api")
      exclude("org.slf4j", "slf4j-log4j12"),
  "org.clojure" % "clojure" % "1.6.0" % "provided",
  "org.specs2" %% "specs2" % "2.3.13" % "test",
  "org.slf4j" % "slf4j-api" % "1.7.12"
)

mainClass in Compile := Some("storm.starter.topology.ExclamationTopology")

mainClass in assembly := Some("storm.starter.topology.ExclamationTopology")

TaskKey[File]("generate-storm") <<= (baseDirectory, fullClasspath in Compile, mainClass in Compile) map { (base, cp, main) =>
  val template = """#!/bin/sh
java -classpath "%s" %s "$@"
"""
  val mainStr = main getOrElse error("No main class specified")
  val contents = template.format(cp.files.absString, mainStr)
  val out = base / "bin/run-main-topology.sh"
  IO.write(out, contents)
  out.setExecutable(true)
  out
}
