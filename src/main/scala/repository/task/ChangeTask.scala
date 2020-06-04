package repository.task

import java.time.{LocalDateTime, ZoneOffset, ZonedDateTime}

import cats.effect.Sync
import models.model.TaskToUpdate
import doobie.Transactor
import doobie.implicits._
import error._
import repository.query.TaskQueries
import cats.implicits._
import doobie.postgres.sqlstate

class ChangeTask[F[_] : Sync](tx: Transactor[F]) {

  def apply(toUpdate: TaskToUpdate, timestamp: LocalDateTime, taskDescription: String, projectId: Long, userId: Long): F[Either[TaskUpdateUnsuccessful.type , Unit]] =
    (for {
      _ <- TaskQueries.deleteTask(taskDescription, projectId, userId, timestamp).run
      update <- TaskQueries.updateByInsert(toUpdate, timestamp).option
    } yield update).map {
      case Some(updateCount) if updateCount > 0 => ().asRight
//      case None  => TaskUpdateUnsuccessful.asLeft //todo remove
      case _  => TaskUpdateUnsuccessful.asLeft
    }
      .transact(tx)
}