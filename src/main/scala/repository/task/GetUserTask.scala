package repository.task

import cats.effect.Sync
import doobie.Transactor
import doobie.implicits._
import repository.queries.Task

class GetUserTask[F[_] : Sync](tx: Transactor[F]) {
  def apply(taskDescription: String, userId: Long) = {
    Task
      .fetchTask(taskDescription, userId)
      .transact(tx)
  }
}
