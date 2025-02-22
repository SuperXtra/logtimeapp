package repository.task

import java.time.{LocalDateTime, ZonedDateTime}

import cats.effect.Sync
import doobie.implicits._
import doobie.util.transactor.Transactor
import error._
import repository.query.TaskQueries

class DeleteTasks[F[+_]: Sync](tx: Transactor[F]) {

  def apply(projectId: Long, deleteTime: LocalDateTime): F[Either[LogTimeAppError, Int]] = {
    TaskQueries
      .deleteTasksForProject(projectId, deleteTime)
      .run
      .transact(tx)
      .attemptSomeSqlState {
        case _ =>TaskDeleteUnsuccessful
      }
  }
}
