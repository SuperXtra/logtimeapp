package repository.task

import java.time.LocalDateTime

import cats.effect.Sync
import doobie.postgres.sqlstate
import doobie.util.transactor.Transactor
import error._
import doobie.implicits._
import repository.query.TaskQueries
import cats.implicits._

class DeleteTask[F[+_] : Sync](tx: Transactor[F]) {

  def apply(taskDescription: String, projectId: Long, userId: Long, deleteTime: LocalDateTime): F[Either[LogTimeAppError, Int]] = {
    TaskQueries
      .deleteTask(taskDescription, projectId, userId, deleteTime)
      .run
      .transact(tx)
      .attemptSomeSqlState {
        case sqlstate.class23.UNIQUE_VIOLATION => TaskDeleteUnsuccessful
      }
    }
}


