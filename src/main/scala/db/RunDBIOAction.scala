package db

import cats.effect.{ContextShift, IO}
import slick.dbio.{DBIOAction, NoStream}
import slick.jdbc.PostgresProfile.api._

object RunDBIOAction {
  class RunDBIOAction[T](dbioAction: DBIOAction[T,NoStream,Nothing]) {
    def exec(implicit database: Database, cs: ContextShift[IO]): IO[T] = {
      IO.fromFuture(IO{
        database.run(dbioAction)
      })
    }
  }
  implicit def runDbioActionInstance[T](dbioAction: DBIOAction[T,NoStream,Nothing]) = new RunDBIOAction[T](dbioAction)
}