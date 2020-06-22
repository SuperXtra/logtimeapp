package repository.task

import java.time.LocalDateTime
import cats.effect.Sync
import error._
import repository.query.TaskQueries
import cats.implicits._
import models._
import org.postgresql.util._
import slick.jdbc.PostgresProfile.api._
import slick.dbio.Effect
import scala.concurrent.ExecutionContext
import ExecutionContext.Implicits.global

import scala.util.{Failure, Success}

class DeleteTask[F[+_] : Sync] {

  def apply(taskDescription: String, projectId: ProjectId, userId: UserId, deleteTime: LocalDateTime): DBIOAction[Either[LogTimeAppError, DeleteCount], NoStream, Effect.Write with Effect] = {
    TaskQueries
      .deleteTask(taskDescription, projectId, userId, deleteTime)
      .asTry
      .flatMap {
        case Failure(ex: PSQLException) => ex match {
          case e: PSQLException if PSQLState.valueOf(e.getServerErrorMessage.getSQLState) == PSQLState.UNIQUE_VIOLATION => DBIO.successful(TaskDeleteUnsuccessful.asLeft)
        }
        case Failure(_) => DBIO.successful(UnknownError.asLeft)
        case Success(value) => DBIO.successful(DeleteCount(value).asRight)
      }
  }
}