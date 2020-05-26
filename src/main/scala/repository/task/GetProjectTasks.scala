package repository.task

import cats.effect.Sync
import doobie.Transactor
import error.{AppError, FetchingTaskForProjectUnsuccessful}
import doobie.implicits._
import models.model.TaskTb
import repository.queries.Task

class GetProjectTasks[F[_] : Sync](tx: Transactor[F]) {

  def apply(projectId: Long): F[Either[AppError, List[TaskTb]]] = {
    Task
      .fetchTasksForProject(projectId)
      .transact(tx)
      .attemptSomeSqlState {
        case _ => FetchingTaskForProjectUnsuccessful
      }
  }

}
