package repository.task

import cats.effect.Sync
import doobie.postgres.sqlstate
import doobie.util.transactor.Transactor
import error._
import doobie.implicits._
import repository.queries.Task

class TaskDelete[F[+_] : Sync](tx: Transactor[F]) {

  def apply(taskDescription: String, projectId: Long, userId: Long): F[Either[AppError, Int]] = {
    Task
      .deleteTask(taskDescription, projectId, userId)
      .run.transact(tx)
      .attemptSomeSqlState {
        case sqlstate.class23.UNIQUE_VIOLATION => TaskDeleteUnsuccessful()
      }
  }
}


