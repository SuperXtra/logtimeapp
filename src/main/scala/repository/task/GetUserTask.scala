package repository.task

import cats.effect.Sync
import doobie.Transactor
import doobie.implicits._
import repository.query.TaskQueries

class GetUserTask[F[_] : Sync](tx: Transactor[F]) {
  def apply(taskDescription: String, userId: Long) = {
    TaskQueries
      .fetchTask(taskDescription, userId)
      .transact(tx)
  }
}
