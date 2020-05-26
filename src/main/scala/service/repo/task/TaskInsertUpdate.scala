package service.repo.task

import cats.effect.Sync
import data.{Queries, UpdateTaskInsert}
import doobie.Transactor
import doobie.implicits._

class TaskInsertUpdate[F[_]: Sync](tx: Transactor[F]) {

  def apply(toUpdate: UpdateTaskInsert) = {
    Queries.Task.insertUpdate(toUpdate).transact(tx)
  }

}
