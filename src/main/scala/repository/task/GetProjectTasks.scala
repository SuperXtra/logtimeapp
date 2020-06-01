package repository.task

import cats.effect.Sync
import doobie.Transactor
import error._
import doobie.implicits._
import models.model.Task
import repository.query.TaskQueries

class GetProjectTasks[F[_] : Sync](tx: Transactor[F]) {

  def apply(projectId: Int): F[Either[AppError, List[Task]]] = {
    TaskQueries
      .fetchTasksForProject(projectId)
      .to[List]
      .transact(tx)
      .attemptSomeSqlState {
        case x => TaskNotFound()
      }
  }

}
