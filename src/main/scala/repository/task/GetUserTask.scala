package repository.task

import cats.effect.Sync
import doobie.Transactor
import doobie.implicits._
import repository.query.TaskQueries
import cats.implicits._
import error._
import models.model._

class GetUserTask[F[_] : Sync](tx: Transactor[F]) {
  def apply(taskDescription: String, userId: Long)= {
    TaskQueries
      .fetchTask(taskDescription, userId)
      .option
      .transact(tx)
  }
}
