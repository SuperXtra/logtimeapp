package service

import akka.event.{MarkerLoggingAdapter, NoMarkerLogging}
import cats.effect.{ContextShift, IO}
import com.typesafe.config.{Config, ConfigFactory}
import config.DatabaseConfig
import db.DatabaseContext
import doobie.util.ExecutionContexts

trait SetUp {
  import slick.jdbc.PostgresProfile.api._
  import pureconfig._
  import pureconfig.generic.auto._

  lazy val databaseConfiguration: Config = ConfigFactory.load("database-configuration.conf")
  lazy val databaseConfig: DatabaseConfig = ConfigSource.fromConfig(databaseConfiguration).loadOrThrow[DatabaseConfig]
  implicit lazy val logger: MarkerLoggingAdapter = NoMarkerLogging
  implicit val cs: ContextShift[IO] = IO.contextShift(ExecutionContexts.synchronous)
  implicit lazy val transactor: Database = DatabaseContext.transactor(databaseConfig)
}
