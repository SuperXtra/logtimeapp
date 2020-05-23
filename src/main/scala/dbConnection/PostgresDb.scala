package dbConnection

import java.util.UUID

import doobie._
import doobie.implicits._
import doobie.util.ExecutionContexts
import cats.effect._
import cats.implicits._
import com.typesafe.config.ConfigFactory
import config.DatabaseConfig
import data.Entities.User
import pureconfig._
import pureconfig.generic.auto._

object PostgresDb {

  implicit val cs = IO.contextShift(ExecutionContexts.synchronous)

  //  val config = ConfigFactory.load("database-config.conf")
  //  val databaseConfig = ConfigSource.fromConfig(config).loadOrThrow[DatabaseConfig]


//  val xa = Transactor.fromDriverManager[IO](
//    databaseConfig.driver,
//    databaseConfig.url,
//    databaseConfig.userName,
//    databaseConfig.password
//  )

  val xa = Transactor.fromDriverManager[IO](
    "org.postgresql.Driver",
    "jdbc:postgresql://ec2-54-75-229-28.eu-west-1.compute.amazonaws.com:5432/d94gigncif0u25",
    "vqidaoxnepgktr",
    "5075aa01fcdf2e9f371b817a92621d5da74acc7a655f3dd29585445f5fd4ffde"
  )
}
