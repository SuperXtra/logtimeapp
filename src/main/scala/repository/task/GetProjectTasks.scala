package repository.task

import cats.effect.Sync
import doobie.Transactor
import error._
import doobie.implicits._
import models.model.TaskTb
import repository.queries.Task

class GetProjectTasks[F[_] : Sync](tx: Transactor[F]) {

  def apply(projectId: Int): F[Either[AppError, List[TaskTb]]] = {
    Task
      .fetchTasksForProject(projectId)
      .to[List]
      .transact(tx)
      .attemptSomeSqlState {
        case x => TaskNotFound(detailErrorMessage = x.value)
      }
  }

}
