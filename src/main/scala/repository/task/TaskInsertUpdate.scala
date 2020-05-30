package repository.task

import cats.effect.Sync
import models.model.TaskToUpdate
import doobie.Transactor
import doobie.implicits._
import repository.query.TaskQueries

class TaskInsertUpdate[F[_] : Sync](tx: Transactor[F]) {

  def apply(toUpdate: TaskToUpdate) = {
    TaskQueries
      .insertUpdate(toUpdate)
      .transact(tx)
  }

}
