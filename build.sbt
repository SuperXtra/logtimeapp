lazy val root = project
  .in(file("."))
  .settings(
    name := "project-management-app",
    version := "0.1",
    scalaVersion := "2.12.2",
    scalacOptions ++= Seq(
      "-encoding", "utf8",
      "-Xfatal-warnings",
      "-deprecation",
      "-unchecked",
      "-feature",
      "-language:implicitConversions",
      "-language:higherKinds",
      "-language:existentials",
      "-language:postfixOps"
    ),
    javacOptions ++= Seq("-source", "1.9", "-target", "1.9", "-encoding", "UTF-8"),
    libraryDependencies ++=
      akka ++
        cats ++
        circe ++
        doobie ++
        authentikat ++
        flyway ++
        pureConfig ++
        scalatest ++
        scalaTestFunSuite ++
        testContainers
  ).configs(IntegrationTest)

lazy val IntegrationTest = config("it") extend Test

lazy val akkaVersion              =  "2.6.5"
lazy val akkaHttpVersion          =  "10.1.12"
lazy val catsVersion              =  "2.0.0"
lazy val circeVersion             =  "0.12.2"
lazy val doobieVersion            =  "0.9.0"

lazy val akkaHttp                 =  Seq("com.typesafe.akka" %% "akka-http"             % akkaHttpVersion)
lazy val akkaSteam                =  Seq("com.typesafe.akka" %% "akka-stream"           % akkaVersion)
lazy val akkaActor                =  Seq("com.typesafe.akka" %% "akka-actor"            % akkaVersion)
lazy val akkaHttpSpray            =  Seq("com.typesafe.akka" %% "akka-http-spray-json"  % akkaHttpVersion)
lazy val akkaCirce                =  Seq("de.heikoseeberger" %% "akka-http-circe"       % "1.31.0")

lazy val akkaHttpTestKit          =  Seq("com.typesafe.akka" %% "akka-http-testkit"     % akkaHttpVersion % "test")
lazy val akkaSteamTestKit         =  Seq("com.typesafe.akka" %% "akka-stream-testkit"   % akkaVersion % "test")

lazy val catsCore                 =  Seq("org.typelevel"     %% "cats-core"             % catsVersion)
lazy val catsEffect               =  Seq("org.typelevel"     %% "cats-effect"           % catsVersion)

lazy val circeParser              =  Seq("io.circe"          %% "circe-parser"          % circeVersion)
lazy val circeGeneric             =  Seq("io.circe"          %% "circe-generic-extras"  % circeVersion)

lazy val doobieCore               =  Seq("org.tpolecat"      %% "doobie-core"           % doobieVersion)
lazy val doobiePostgres           =  Seq("org.tpolecat"      %% "doobie-postgres"       % doobieVersion)
lazy val doobieSpecs2             =  Seq("org.tpolecat"      %% "doobie-specs2"         % doobieVersion % "test")
lazy val doobieScalaTest          =  Seq("org.tpolecat"      %% "doobie-scalatest"      % doobieVersion % "test,it")

lazy val authentikat              =  Seq("com.jason-goodwin" %% "authentikat-jwt" % "0.4.5")

lazy val flyway                   =  Seq("org.flywaydb"      % "flyway-core"           % "6.4.2")
lazy val pureConfig               =  Seq("com.github.pureconfig" %% "pureconfig"        % "0.12.2")

lazy val scalatest                =  Seq("org.scalatest"     %% "scalatest"             % "3.1.1" % "test,it")
lazy val scalaTestFunSuite        =  Seq("org.scalatest"     %% "scalatest-funsuite"    % "3.3.0-SNAP2" % "test")

lazy val testContainersScala      =  Seq("com.dimafeng"      %% "testcontainers-scala-scalatest" % "0.37.0" % "it")
lazy val testContainersPostgres   =  Seq("com.dimafeng"      %% "testcontainers-scala-postgresql" % "0.37.0" % "it")

lazy val akka =
  akkaHttp ++
    akkaActor ++
    akkaSteam ++
    akkaHttpSpray ++
    akkaCirce ++
    akkaHttpTestKit ++
    akkaSteamTestKit

lazy val cats =
  catsCore ++
    catsEffect

lazy val circe =
  circeParser ++
    circeGeneric

lazy val doobie =
  doobieCore ++
    doobiePostgres ++
    doobieScalaTest ++
    doobieSpecs2

lazy val testContainers =
  testContainersScala ++
    testContainersPostgres