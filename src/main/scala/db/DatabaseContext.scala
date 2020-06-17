package db

import cats.effect._
import config.DatabaseConfig
import doobie._
import doobie.util.ExecutionContexts

object DatabaseContext {
  def transactor(databaseConfig: DatabaseConfig)(implicit cs: ContextShift[IO]) =
      Transactor.fromDriverManager[IO](
        databaseConfig.driver,
        databaseConfig.url,
        databaseConfig.userName,
        databaseConfig.password
      )
}
