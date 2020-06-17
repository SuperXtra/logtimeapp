package db

import cats.implicits._
import cats.effect._
import javax.sql.DataSource
import org.flywaydb
import org.flywaydb.core.Flyway

class InitializeDatabase[F[_]: Sync] {
  def apply(url: String, user: String, password: String): F[Unit] = Sync[F].delay {
    val flyWay = Flyway.configure()
      .dataSource(url: String, user: String, password: String)
      .load()
    flyWay.migrate()
    ()
  }
}