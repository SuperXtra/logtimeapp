package repository.task

import java.time.ZonedDateTime

import cats.effect.Sync
import doobie.implicits._
import doobie.util.transactor.Transactor
import error._
import repository.queries.Task

class DeleteTasks[F[+_]: Sync](tx: Transactor[F]) {

  def apply(projectId: Long, deleteTime: ZonedDateTime): F[Either[AppError, Int]] = {
    Task
      .deleteTasksForProject(projectId, deleteTime)
      .run
      .transact(tx)
      .attemptSomeSqlState(x =>TaskDeleteUnsuccessful(detailErrorMessage = x.value))
  }
}
