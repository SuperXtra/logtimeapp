package repository.task

import cats.effect.Sync
import models.model.TaskToUpdate
import doobie.Transactor
import doobie.implicits._
import errorMessages.ProjectUpdateUnsuccessful
import repository.query.TaskQueries
import cats.implicits._

class TaskInsertUpdate[F[_] : Sync](tx: Transactor[F]) {

  def apply(toUpdate: TaskToUpdate) = {
    TaskQueries
      .insertUpdate(toUpdate)
      .option
      .map {
        case Some(x) if x > 0 => ().asRight
        case None  => ProjectUpdateUnsuccessful().asLeft
        case _  => ProjectUpdateUnsuccessful().asLeft
      }
      .transact(tx)
  }

}
