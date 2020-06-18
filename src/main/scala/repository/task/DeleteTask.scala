package repository.task

import java.time.LocalDateTime

import cats.effect.Sync
import doobie.postgres.sqlstate
import doobie.util.transactor.Transactor
import error._
import doobie.implicits._
import repository.query.TaskQueries
import cats.implicits._
import models.{DeleteCount, ProjectId, UserId}

class DeleteTask[F[+_] : Sync](tx: Transactor[F]) {

  def apply(taskDescription: String, projectId: ProjectId, userId: UserId, deleteTime: LocalDateTime): F[Either[LogTimeAppError, DeleteCount]] = {
    TaskQueries
      .deleteTask(taskDescription, projectId, userId, deleteTime)
      .run
      .map(DeleteCount)
      .transact(tx)
      .attemptSomeSqlState {
        case sqlstate.class23.UNIQUE_VIOLATION => TaskDeleteUnsuccessful
      }
    }
}


