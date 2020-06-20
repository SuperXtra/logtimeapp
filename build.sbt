lazy val root = project
  .in(file("."))
  .settings(
    name := "log-time-app",
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
    Defaults.itSettings,
    javacOptions ++= Seq("-source", "1.9", "-target", "1.9", "-encoding", "UTF-8"),
    libraryDependencies ++=
      akka ++
        cats ++
        circe ++
        doobie ++
        authentikat ++
        flyway ++
        macWire ++
        pureConfig ++
        scalatest ++
        scalaTestFunSuite ++
        testContainers
  ).configs(IntegrationTest)

lazy val akkaVersion              =  "2.6.5"
lazy val akkaHttpVersion          =  "10.1.12"
lazy val catsVersion              =  "2.0.0"
lazy val circeVersion             =  "0.12.2"
lazy val doobieVersion            =  "0.9.0"
lazy val akkaCirceVersion         =  "1.31.0"
lazy val authentikatVersion       =  "0.4.5"
lazy val flywayVersion            =  "6.4.2"
lazy val pureConfigVersion        =  "0.12.2"
lazy val scalatestVersion         =  "3.1.1"
lazy val scalaTestFunSuiteVersion =  "3.3.0-SNAP2"
lazy val testContVersion          =  "0.37.0"
lazy val testContPostgresVersion  =  "0.37.0"
lazy val macWireVersion           =  "2.3.6"
lazy val akkaSlickVersion         =  "3.3.2"

lazy val akkaHttp                 =  Seq("com.typesafe.akka"      %% "akka-http"                        % akkaHttpVersion                       )
lazy val akkaSteam                =  Seq("com.typesafe.akka"      %% "akka-stream"                      % akkaVersion                           )
lazy val akkaActor                =  Seq("com.typesafe.akka"      %% "akka-actor"                       % akkaVersion                           )
lazy val akkaHttpSpray            =  Seq("com.typesafe.akka"      %% "akka-http-spray-json"             % akkaHttpVersion                       )
lazy val akkaCirce                =  Seq("de.heikoseeberger"      %% "akka-http-circe"                  % akkaCirceVersion                      )
lazy val akkaSlick                =  Seq("com.typesafe.slick"     %% "slick"                            % akkaSlickVersion                      )

lazy val akkaHttpTestKit          =  Seq("com.typesafe.akka"      %% "akka-http-testkit"                % akkaHttpVersion           % "test"    )
lazy val akkaSteamTestKit         =  Seq("com.typesafe.akka"      %% "akka-stream-testkit"              % akkaVersion               % "test"    )

lazy val catsCore                 =  Seq("org.typelevel"          %% "cats-core"                        % catsVersion                           )
lazy val catsEffect               =  Seq("org.typelevel"          %% "cats-effect"                      % catsVersion                           )

lazy val circeParser              =  Seq("io.circe"               %% "circe-parser"                     % circeVersion                          )
lazy val circeGeneric             =  Seq("io.circe"               %% "circe-generic-extras"             % circeVersion                          )

lazy val doobieCore               =  Seq("org.tpolecat"           %% "doobie-core"                      % doobieVersion                         )
lazy val doobiePostgres           =  Seq("org.tpolecat"           %% "doobie-postgres"                  % doobieVersion                         )
lazy val doobieSpecs2             =  Seq("org.tpolecat"           %% "doobie-specs2"                    % doobieVersion             % "test"    )
lazy val doobieScalaTest          =  Seq("org.tpolecat"           %% "doobie-scalatest"                 % doobieVersion             % "test,it" )

lazy val authentikat              =  Seq("com.jason-goodwin"      %% "authentikat-jwt"                  % authentikatVersion                    )

lazy val flyway                   =  Seq("org.flywaydb"           % "flyway-core"                       % flywayVersion                         )
lazy val pureConfig               =  Seq("com.github.pureconfig"  %% "pureconfig"                       % pureConfigVersion                     )

lazy val scalatest                =  Seq("org.scalatest"          %% "scalatest"                        % scalatestVersion          % "test,it" )
lazy val scalaTestFunSuite        =  Seq("org.scalatest"          %% "scalatest-funsuite"               % scalaTestFunSuiteVersion  % "test"    )

lazy val testContainersScala      =  Seq("com.dimafeng"           %% "testcontainers-scala-scalatest"   % testContVersion           % "it"      )
lazy val testContainersPostgres   =  Seq("com.dimafeng"           %% "testcontainers-scala-postgresql"  % testContPostgresVersion   % "it"      )

lazy val macWireMacros            =  Seq("com.softwaremill.macwire" %% "macros"                         % macWireVersion            % "provided")
lazy val macrosAkka               =  Seq("com.softwaremill.macwire" %% "macrosakka"                     % macWireVersion            % "provided")
lazy val macWireUtil              =  Seq("com.softwaremill.macwire" %% "util"                           % macWireVersion                        )
lazy val macWireProxy             =  Seq("com.softwaremill.macwire" %% "proxy"                          % macWireVersion                        )

lazy val akka =
  akkaHttp ++
    akkaActor ++
    akkaSteam ++
    akkaHttpSpray ++
    akkaCirce ++
    akkaHttpTestKit ++
    akkaSteamTestKit ++
    akkaSlick

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

lazy val macWire =
  macWireMacros ++
    macrosAkka ++
    macWireUtil ++
    macWireProxy