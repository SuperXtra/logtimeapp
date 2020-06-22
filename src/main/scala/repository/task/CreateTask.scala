package repository.task

import java.time.LocalDateTime
import cats.implicits._
import cats.effect._
import models.request.LogTaskRequest
import error._
import models.{ProjectId, TaskId, UserId}
import repository.query.TaskQueries
import slick.jdbc.PostgresProfile.api._
import org.postgresql.util.{PSQLException, _}
import slick.dbio.Effect

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}
import ExecutionContext.Implicits.global


class CreateTask[F[_] : Sync] {

  def apply(create: LogTaskRequest, projectId: ProjectId, userId: UserId, startTime: LocalDateTime): DBIOAction[Either[LogTimeAppError, TaskId], NoStream, Effect.Write with Effect] =
    TaskQueries
      .insert(create, projectId, userId, startTime)
      .asTry
      .flatMap {
        case Failure(ex: PSQLException) =>
          ex match {
            case e: PSQLException if e.getServerErrorMessage.getSQLState == "23P01" => DBIO.successful(TaskNotCreatedExclusionViolation.asLeft)
            case e: PSQLException if e.getServerErrorMessage.getSQLState == "23505" => DBIO.successful(TaskNameExists.asLeft)
          }
        case Failure(_) => DBIO.successful(UnknownError.asLeft)
        case Success(value)
        => DBIO.successful(value.asRight)
      }
}