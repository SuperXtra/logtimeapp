package repository.task

import java.time._
import cats.effect.Sync
import error._
import models._
import repository.query.TaskQueries
import slick.jdbc.PostgresProfile.api._
import slick.dbio.Effect
import scala.concurrent.ExecutionContext
import ExecutionContext.Implicits.global
import cats.implicits._
import scala.util.{Failure, Success}

class DeleteTasks[F[+_] : Sync] {

  def apply(projectId: ProjectId, deleteTime: LocalDateTime): DBIOAction[Either[LogTimeAppError, Int], NoStream, Effect.Write with Effect] = {
    TaskQueries
      .deleteTasksForProject(projectId, deleteTime)
      .asTry
      .flatMap {
        case Failure(exception) => DBIO.successful(TaskDeleteUnsuccessful.asLeft)
        case Success(value) => DBIO.successful(value.asRight)
      }
  }
}
