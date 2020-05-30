package repository.task

import java.sql.Timestamp
import java.time.LocalDateTime

import cats.implicits._
import cats.effect._
import models.request.LogTaskRequest
import doobie._
import doobie.postgres.sqlstate
import error._
import doobie.implicits._
import doobie.implicits.javasql._
import doobie.implicits.javatime._
import repository.query.TaskQueries

class InsertTask[F[_] : Sync](tx: Transactor[F]) {

  def apply(create: LogTaskRequest, projectId: Long, userId: Long): F[Either[AppError, Int]] = {
    TaskQueries
      .insert(create, projectId, userId)
      .unique
      .transact(tx)
      .attemptSomeSqlState {
        case sqlstate.class23.EXCLUSION_VIOLATION => TaskNotCreated("User can not log task with overlapping time")
        case sqlstate.class23.UNIQUE_VIOLATION => TaskNameExists()
      }
  }
}