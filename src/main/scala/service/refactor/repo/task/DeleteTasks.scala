package service.refactor.repo.task

import java.time.ZonedDateTime

import cats.effect.Sync
import data.Queries
import doobie.implicits._
import doobie.util.transactor.Transactor
import error.{AppError, DeleteProjectUnsuccessful}

class DeleteTasks[F[+_]: Sync](tx: Transactor[F]) {

  def apply(projectId: Long, deleteTime: ZonedDateTime): F[Either[AppError, Int]] = {
    Queries
      .Task
      .deleteTasksForProject(projectId, deleteTime)
      .run
      .transact(tx)
      .attemptSomeSqlState(_ => DeleteProjectUnsuccessful)
  }
}
