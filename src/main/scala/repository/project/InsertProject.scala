package repository.project

import cats.effect.Sync
import doobie.implicits._
import doobie.postgres.sqlstate
import doobie.util.transactor.Transactor
import errorMessages._
import repository.query.ProjectQueries


class InsertProject[F[+_] : Sync](tx: Transactor[F]) {
  def apply(projectName: String, userId: Long): F[Either[AppBusinessError, Int]] =
    ProjectQueries
      .insert(projectName, userId)
      .unique
      .transact(tx)
      .attemptSomeSqlState {
        case sqlstate.class23.UNIQUE_VIOLATION => ProjectNotCreated()
      }
}