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
import models.{ProjectId, UserId}

class ChangeTask[F[_] : Sync](tx: Transactor[F]) {

  def apply(toUpdate: TaskToUpdate, timestamp: LocalDateTime, taskDescription: String, projectId: ProjectId, userId: UserId): F[Either[LogTimeAppError, Unit]] =
    (for {
      _ <- TaskQueries.deleteTask(taskDescription, projectId, userId, timestamp).run
      update <- TaskQueries.updateByInsert(toUpdate, timestamp).option
    } yield update)
      .transact(tx)
      .attemptSomeSqlState {
        case sqlstate.class23.UNIQUE_VIOLATION => TaskNameExists
        case sqlstate.class23.EXCLUSION_VIOLATION => TaskNotCreatedExclusionViolation
      }.map {
      case Left(error) => error.asLeft
      case Right(updateCount) => updateCount match {
        case None => TaskUpdateUnsuccessful.asLeft
        case Some(id) => ().asRight
      }
    }
}