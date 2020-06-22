package repository.task

import cats.effect.Sync
import doobie._
import doobie.implicits._
import models.TaskId
import models.model.Task
import repository.query.TaskQueries
import slick.jdbc.PostgresProfile.api._
import slick.dbio.Effect

import scala.concurrent.ExecutionContext
import ExecutionContext.Implicits.global
import cats.implicits._
import error.{LogTimeAppError, TaskNotCreated}

import scala.util.{Failure, Success}

class GetTask[F[_] : Sync] {
  def apply(id: TaskId): DBIOAction[Either[LogTimeAppError, Task], NoStream, Effect.Read with Effect] =
    TaskQueries
      .getTaskById(id)
    .asTry
    .flatMap {
      case Failure(exception) => DBIO.successful(TaskNotCreated.asLeft)
      case Success(value) => value match {
        case Some(value) => DBIO.successful(value.asRight)
        case None =>DBIO.successful(TaskNotCreated.asLeft)
      }
    }
}
