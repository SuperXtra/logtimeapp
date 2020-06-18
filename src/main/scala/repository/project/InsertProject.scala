package repository.project

import cats.effect.Sync
import doobie.implicits._
import doobie.postgres.sqlstate
import doobie.util.transactor.Transactor
import error._
import models.{ProjectId, UserId}
import repository.query.ProjectQueries


class InsertProject[F[+_] : Sync](tx: Transactor[F]) {
  def apply(projectName: String, userId: UserId): F[Either[LogTimeAppError, ProjectId]] =
    ProjectQueries
      .insert(projectName, userId)
      .unique
      .map(ProjectId)
      .transact(tx)
      .attemptSomeSqlState {
        case sqlstate.class23.UNIQUE_VIOLATION => ProjectNotCreated
      }
}