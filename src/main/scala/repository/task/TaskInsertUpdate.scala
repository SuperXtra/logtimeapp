package repository.task

import java.time.{LocalDateTime, ZoneOffset, ZonedDateTime}

import cats.effect.Sync
import models.model.TaskToUpdate
import doobie.Transactor
import doobie.implicits._
import errorMessages.{AppBusinessError, ProjectUpdateUnsuccessful, TaskDeleteUnsuccessful}
import repository.query.TaskQueries
import cats.implicits._
import doobie.postgres.sqlstate

class TaskInsertUpdate[F[_] : Sync](tx: Transactor[F]) {

  def apply(toUpdate: TaskToUpdate, timestamp: LocalDateTime, taskDescription: String, projectId: Long, userId: Long): F[Either[ProjectUpdateUnsuccessful, Unit]] =

    (for {
      _ <- TaskQueries.deleteTask(taskDescription, projectId, userId, timestamp).run
      update <- TaskQueries.insertUpdate(toUpdate, timestamp).option
    } yield update).map {
      case Some(x) if x > 0 => ().asRight
      case None  => ProjectUpdateUnsuccessful().asLeft
      case _  => ProjectUpdateUnsuccessful().asLeft
    }
      .transact(tx)
}
