name := "it"

version := "0.1"

scalaVersion := "2.13.2"

lazy val doobieVersion = "0.8.8"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http"   % "10.1.12",
  "org.typelevel" %% "cats-core"   % "2.0.0",
  "org.tpolecat" %% "doobie-core"     % doobieVersion,
  "org.tpolecat" %% "doobie-postgres" % doobieVersion,
  "com.github.pureconfig" %% "pureconfig" % "0.12.2",
  "org.tpolecat" %% "doobie-specs2"   % doobieVersion,
  "org.scalatest" %% "scalatest-funsuite" % "3.3.0-SNAP2",
  "org.tpolecat" %% "doobie-scalatest" % "0.9.0" % Test,
  "com.typesafe.akka" %% "akka-stream" % "2.5.26",
  "com.typesafe.akka" %% "akka-http-spray-json" % "10.1.12"
)