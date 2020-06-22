package repository.task

import cats.effect.Sync
import doobie.Transactor
import doobie.implicits._
import repository.query.TaskQueries
import cats.implicits._
import error._
import models.UserId
import models.model._
import slick.jdbc.PostgresProfile.api._
import slick.dbio.Effect

import scala.concurrent.ExecutionContext
import ExecutionContext.Implicits.global
import cats.implicits._
import error.{LogTimeAppError, TaskNotCreated}

import scala.util.{Failure, Success}

class GetUserTask[F[_] : Sync]{
  def apply(taskDescription: String, userId: UserId): DBIOAction[Either[LogTimeAppError, Task], NoStream, Effect.Read with Effect] = {
    TaskQueries
      .fetchTask(taskDescription, userId)
      .asTry
      .flatMap {
        case Failure(_) => DBIO.successful(TaskNotFound.asLeft)
        case Success(value) => value match {
          case Some(task) => DBIO.successful(task.asRight)
          case None => DBIO.successful(TaskNotFound.asLeft)
        }
      }
  }
}
