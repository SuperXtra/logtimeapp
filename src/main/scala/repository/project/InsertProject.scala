package repository.project

import cats.effect.Sync
import doobie.implicits._
import doobie.postgres.sqlstate
import doobie.util.transactor.Transactor
import error._
import repository.queries.Project


class InsertProject[F[+_] : Sync](tx: Transactor[F]) {
  def apply(projectName: String, userId: Long): F[Either[AppError, Long]] =
    Project
      .insert(projectName, userId)
      .unique
      .transact(tx)
      .attemptSomeSqlState {
        case sqlstate.class23.UNIQUE_VIOLATION => ProjectNotCreated(s"Cannot create project, given name ${projectName} exists already")
      }
}