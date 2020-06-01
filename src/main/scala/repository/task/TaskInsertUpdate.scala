package repository.task

import java.time.{LocalDateTime, ZoneOffset, ZonedDateTime}

import cats.effect.Sync
import models.model.TaskToUpdate
import doobie.Transactor
import doobie.implicits._
import errorMessages.ProjectUpdateUnsuccessful
import repository.query.TaskQueries
import cats.implicits._

class TaskInsertUpdate[F[_] : Sync](tx: Transactor[F]) {

  def apply(toUpdate: TaskToUpdate, created: LocalDateTime) = {
    TaskQueries
      .insertUpdate(toUpdate, created)
      .option
      .map {
        case Some(x) if x > 0 => ().asRight
        case None  => ProjectUpdateUnsuccessful().asLeft
        case _  => ProjectUpdateUnsuccessful().asLeft
      }
      .transact(tx)
  }

}
