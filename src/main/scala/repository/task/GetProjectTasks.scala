package repository.task

import cats.effect.Sync
import error._
import models.ProjectId
import models.model.Task
import repository.query.TaskQueries
import slick.jdbc.PostgresProfile.api._
import slick.dbio.Effect

import scala.concurrent.ExecutionContext
import ExecutionContext.Implicits.global
import cats.implicits._

import scala.util.{Failure, Success}

class GetProjectTasks[F[_] : Sync] {

  def apply(projectId: ProjectId): DBIOAction[Either[LogTimeAppError, List[Task]], NoStream, Effect.Read with Effect] = {
    TaskQueries
      .fetchTasksForProject(projectId)
      .asTry
      .flatMap {
        case Failure(_) => DBIO.successful(TaskNotFound.asLeft)
        case Success(value) => DBIO.successful(value.toList.asRight)
      }
  }
}