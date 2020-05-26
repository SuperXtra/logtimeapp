package service.repo.task

import java.sql.Timestamp
import java.time.LocalDateTime

import cats.implicits._
import cats.effect._
import data.Entities.Task
import data.LogTaskModel
import doobie._
import doobie.postgres.sqlstate
import error._
import doobie.implicits._
import doobie.implicits.javasql._
import doobie.implicits.javatime._

class InsertTask[F[_]: Sync](tx: Transactor[F]) {

  def apply(create: LogTaskModel, projectId: Long, userId: Long): F[Either[AppError, Long]] = {

    // TODO replace with implicit parameters
    val start = create.startTime
    val end = start.plusMinutes(create.durationTime)

    sql"""insert into tb_task (
         |    project_id,
         |    user_id,
         |    task_description,
         |    start_time,
         |    end_time,
         |    volume,
         |    comment
         |  ) VALUES (
         |    ${projectId},
         |    ${userId},
         |    ${create.taskDescription},
         |    ${start.toLocalDateTime},
         |    ${end.toLocalDateTime},
         |    ${create.volume},
         |    ${create.comment}
         |  ) returning id"""
      .query[Long]
      .unique
      .transact(tx)
      .attemptSomeSqlState{
        case sqlstate.class23.EXCLUSION_VIOLATION => CannotLogNewTaskWithTheOverlappingTimeRangeForTheSameUser
        case sqlstate.class23.UNIQUE_VIOLATION => CannotLogNewTaskWithDuplicateTaskDescriptionUnderTheSameProject
      }
  }
}