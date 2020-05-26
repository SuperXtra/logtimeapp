package service.repo.task

import cats.effect.Sync
import data.Queries
import doobie.Transactor
import doobie.implicits._

class GetUserTask[F[_]: Sync](tx: Transactor[F]) {
  def apply(taskDescription: String, userId: Long) = {
    Queries.
      Task
      .fetchTask(taskDescription, userId)
      .transact(tx)
  }

}
