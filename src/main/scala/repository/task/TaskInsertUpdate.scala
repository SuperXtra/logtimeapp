package repository.task

import cats.effect.Sync
import models.model.TaskToUpdate
import doobie.Transactor
import doobie.implicits._
import repository.queries.Task

class TaskInsertUpdate[F[_] : Sync](tx: Transactor[F]) {

  def apply(toUpdate: TaskToUpdate) = {
    Task
      .insertUpdate(toUpdate)
      .transact(tx)
  }

}
