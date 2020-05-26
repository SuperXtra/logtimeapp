package service.repo.task

import cats.effect.Sync
import data.Entities.Task
import data.Queries
import doobie.Transactor
import error.{AppError, FetchingTaskForProjectUnsuccessful}
import doobie.implicits._

class GetProjectTasks[F[_]: Sync](tx: Transactor[F]) {

  def apply(projectId: Long): F[Either[AppError, List[Task]]] = {
    Queries.Task.fetchTasksForProject(projectId).transact(tx).attemptSomeSqlState {
      case _ => FetchingTaskForProjectUnsuccessful
    }
  }

}
