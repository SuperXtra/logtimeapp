package service.repo.project

import cats.effect.Sync
import data.Queries
import doobie.implicits._
import doobie.postgres.sqlstate
import doobie.util.transactor.Transactor
import error.{AppError, CannotLogNewTaskWithDuplicateTaskDescriptionUnderTheSameProject, CannotLogNewTaskWithTheOverlappingTimeRangeForTheSameUser}


class InsertProject[F[+_] : Sync](tx: Transactor[F]) {
  def apply(projectName: String, userId: Long): F[Either[AppError, Long]] =
    Queries
      .Project
      .insert(projectName, userId).transact(tx)
      .attemptSomeSqlState {
        case sqlstate.class23.EXCLUSION_VIOLATION => CannotLogNewTaskWithTheOverlappingTimeRangeForTheSameUser
        case sqlstate.class23.UNIQUE_VIOLATION => CannotLogNewTaskWithDuplicateTaskDescriptionUnderTheSameProject
      }
}