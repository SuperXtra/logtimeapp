package repository.task

import java.time._
import cats.effect.Sync
import models.model.TaskToUpdate
import error._
import repository.query.TaskQueries
import cats.implicits._
import models.{ProjectId, UserId}
import scala.concurrent._
import ExecutionContext.Implicits.global
import scala.util.{Failure, Success}
import slick.jdbc.PostgresProfile.api._
import org.postgresql.util.{PSQLException, _}

class ChangeTask[F[_] : Sync] {

  def apply(toUpdate: TaskToUpdate, timestamp: LocalDateTime, taskDescription: String, projectId: ProjectId, userId: UserId): DBIOAction[Either[LogTimeAppError, Unit], NoStream, Effect.Write with Effect.Transactional with Effect] =
    (for {
      _ <- TaskQueries.deleteTask(taskDescription, projectId, userId, timestamp)
      update <- TaskQueries.updateByInsert(toUpdate, timestamp)
    } yield update).transactionally
      .asTry
      .flatMap {
        case Failure(ex: PSQLException) => ex match {
          case  e: PSQLException if PSQLState.valueOf(e.getServerErrorMessage.getSQLState) == PSQLState.EXCLUSION_VIOLATION => DBIO.successful(TaskNameExists.asLeft)
          case  e: PSQLException if PSQLState.valueOf(e.getServerErrorMessage.getSQLState) == PSQLState.UNIQUE_VIOLATION => DBIO.successful(TaskNotCreatedExclusionViolation.asLeft)
        }
        case Failure(_) => DBIO.successful(TaskNotFound.asLeft)
        case Success(_) => DBIO.successful(().asRight)
      }
}