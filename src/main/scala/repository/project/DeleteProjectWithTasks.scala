package repository.project

import java.time._

import cats.effect._
import error._
import models.{ProjectId, UserId}
import repository.query._
import cats.implicits._
import slick.dbio.Effect
import scala.util.{Failure, Success}
import slick.jdbc.PostgresProfile.api._
import scala.concurrent._
import ExecutionContext.Implicits.global

class DeleteProjectWithTasks[F[+_] : Sync] {
  def apply(userId: UserId, projectName: String, projectId: ProjectId, deleteTime: LocalDateTime): DBIOAction[Either[LogTimeAppError, Unit], NoStream, Effect.Write with Effect.Transactional with Effect] = {
    (for {
      _ <- ProjectQueries.deactivate(userId, projectName, deleteTime)
      _ <- TaskQueries.deleteTasksForProject(projectId, deleteTime)
    } yield ()).transactionally
      .asTry
      .flatMap {
        case Failure(_) =>
          DBIO.successful(ProjectDeleteUnsuccessful.asLeft)
        case Success(_) => DBIO.successful(().asRight)
      }
  }
}