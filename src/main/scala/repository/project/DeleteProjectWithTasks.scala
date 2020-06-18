package repository.project

import java.time.{LocalDateTime, ZonedDateTime}

import cats.effect._
import doobie.implicits._
import doobie.util.transactor.Transactor
import error._
import models.{ProjectId, UserId}
import models.model.Project
import models.request.DeleteProjectRequest
import repository.query.{ProjectQueries, TaskQueries, UserQueries}

class DeleteProjectWithTasks[F[+_] : Sync](tx: Transactor[F]) {

  def apply(userId: UserId, projectName: String, projectId: ProjectId, deleteTime: LocalDateTime): F[Either[LogTimeAppError, Unit]] = {

    (for {
      _ <- ProjectQueries.deactivate(userId, projectName, deleteTime).run
      _ <- TaskQueries.deleteTasksForProject(projectId, deleteTime).run
    } yield ()).transact(tx)
      .attemptSomeSqlState {
        case _ => ProjectDeleteUnsuccessful
      }
  }
}
