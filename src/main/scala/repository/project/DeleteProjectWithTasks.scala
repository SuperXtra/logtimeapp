package repository.project

import java.time.ZonedDateTime

import cats.effect._
import doobie.implicits._
import doobie.util.transactor.Transactor
import errorMessages._
import models.model.Project
import models.request.DeleteProjectRequest
import repository.query.{ProjectQueries, TaskQueries, UserQueries}

class DeleteProjectWithTasks[F[+_] : Sync](tx: Transactor[F]) {

  def apply(userId: Int, projectName: String, projectId: Int, deleteTime: ZonedDateTime): F[Either[AppBusinessError, Unit]] = {

    (for {
      _ <- ProjectQueries.deleteProject(userId, projectName, deleteTime).run
      _ <- TaskQueries.deleteTasksForProject(projectId, deleteTime).run
    } yield ()).transact(tx)
      .attemptSomeSqlState {
        case _ => ProjectDeleteUnsuccessful()
      }
  }


}
