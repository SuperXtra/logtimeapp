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
  "org.tpolecat" %% "doobie-specs2"   % doobieVersion
)