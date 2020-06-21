package db

import cats.effect._
import config.DatabaseConfig
import slick.jdbc.PostgresProfile.api._

object DatabaseContext {

  def transactor[A](databaseConfig: DatabaseConfig)
                        (implicit cs: ContextShift[IO]) = {
    Database.forURL(
      databaseConfig.url,
      databaseConfig.userName,
      databaseConfig.password,
      null,
      databaseConfig.driver
    )
  }
}