package repository.queries

import cats.effect.IO
import com.typesafe.config.ConfigFactory
import config.DatabaseConfig
import doobie.util.ExecutionContexts
import doobie.util.transactor.Transactor
import org.scalatest.funsuite.AnyFunSuite
import org.specs2.matcher.Matchers
import pureconfig.ConfigSource
import pureconfig._
import pureconfig.generic.auto._

abstract class QueryTest extends AnyFunSuite with Matchers with doobie.scalatest.IOChecker {

  val config = ConfigFactory.load("database-configuration.conf")
  val databaseConfig = ConfigSource.fromConfig(config).loadOrThrow[DatabaseConfig]

  implicit val cs = IO.contextShift(ExecutionContexts.synchronous)
  val transactor = Transactor.fromDriverManager[IO](
    databaseConfig.driver,
    databaseConfig.url,
    databaseConfig.userName,
    databaseConfig.password
  )

}
