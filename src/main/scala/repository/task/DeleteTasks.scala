package repository.task

import java.time.ZonedDateTime

import cats.effect.Sync
import doobie.implicits._
import doobie.util.transactor.Transactor
import errorMessages._
import repository.query.TaskQueries

class DeleteTasks[F[+_]: Sync](tx: Transactor[F]) {

  def apply(projectId: Long, deleteTime: ZonedDateTime): F[Either[AppBusinessError, Int]] = {
    TaskQueries
      .deleteTasksForProject(projectId, deleteTime)
      .run
      .transact(tx)
      .attemptSomeSqlState {
        case x =>TaskDeleteUnsuccessful()
      }
  }
}
