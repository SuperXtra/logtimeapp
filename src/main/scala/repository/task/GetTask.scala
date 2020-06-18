package repository.task

import cats.effect.Sync
import doobie._
import doobie.implicits._
import models.TaskId
import models.model.Task
import repository.query.TaskQueries

class GetTask[F[_] : Sync](tx: Transactor[F]) {
  def apply(id: TaskId): F[Option[Task]] =
    TaskQueries
      .getTaskById(id)
      .option
      .transact(tx)
}
