package repository.task

import java.time.{LocalDateTime, ZonedDateTime}

import cats.effect.Sync
import doobie.implicits._
import doobie.util.transactor.Transactor
import error._
import models.{DeleteCount, ProjectId}
import repository.query.TaskQueries

class DeleteTasks[F[+_] : Sync](tx: Transactor[F]) {

  def apply(projectId: ProjectId, deleteTime: LocalDateTime): F[Either[LogTimeAppError, DeleteCount]] = {
    TaskQueries
      .deleteTasksForProject(projectId, deleteTime)
      .run
      .map(DeleteCount)
      .transact(tx)
      .attemptSomeSqlState {
        case _ => TaskDeleteUnsuccessful
      }
  }
}
