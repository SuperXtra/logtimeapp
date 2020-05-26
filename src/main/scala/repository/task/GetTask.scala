package repository.task

import cats.effect.Sync
import doobie._
import doobie.implicits._
import models.model.TaskTb
import repository.queries.Task

class GetTask[F[_] : Sync](tx: Transactor[F]) {

  def apply(id: Long): F[Option[TaskTb]] =
    Task
      .selectLastInsertedTask(id)
      .transact(tx)
}
