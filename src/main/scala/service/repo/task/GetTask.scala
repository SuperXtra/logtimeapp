package service.repo.task

import cats.effect.Sync
import data.Entities.Task
import data.Queries
import doobie._
import doobie.implicits._

class GetTask[F[_]: Sync](tx: Transactor[F]) {

  def apply(id: Long): F[Option[Task]] =
    Queries.Task.selectLastInsertedTask(id).transact(tx)
}
