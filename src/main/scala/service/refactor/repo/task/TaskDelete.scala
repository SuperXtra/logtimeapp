package service.refactor.repo.task

import cats.effect.Sync
import data.Queries
import doobie.postgres.sqlstate
import doobie.util.transactor.Transactor
import error.{AppError, CannotChangeNameGivenTaskExistsAlready}
import doobie.implicits._

class TaskDelete[F[+_]: Sync](tx: Transactor[F]) {

def apply(taskDescription: String, projectId: Long, userId: Long): F[Either[AppError, Int]] = {
  Queries
    .Task
    .deleteTask(taskDescription, projectId, userId)
    .run.transact(tx)
    .attemptSomeSqlState {
    case sqlstate.class23.UNIQUE_VIOLATION => CannotChangeNameGivenTaskExistsAlready
  }
}
}


