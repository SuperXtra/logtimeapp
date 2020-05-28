package repository.queries

import java.time.{ZoneOffset, ZonedDateTime}

import models.model.{TaskTb, TaskToUpdate}
import models.request.LogTaskRequest
import java.time.{ZoneOffset, ZonedDateTime}

import cats.implicits._
import doobie.free.connection.ConnectionIO
import doobie.util.log.LogHandler
import doobie.implicits.javatime._
import doobie.{Fragment, Update0}
import doobie.implicits._
import doobie.util.query.Query0
import models.model._
import models.request._
import models.responses.FinalReport

object Task {
  implicit val han = LogHandler.jdkLogHandler


  def insertUpdate(update: TaskToUpdate) = {
    val start = update.startTime
    val end = start.plusMinutes(update.duration)
    sql"insert into tb_task (project_id, user_id, create_time, task_description, start_time, end_time, duration, volume, comment) VALUES (${update.projectId}, ${update.userId}, ${ZonedDateTime.now(ZoneOffset.UTC).toLocalDateTime}, ${update.taskDescription}, ${start.toLocalDateTime}, ${end.toLocalDateTime}, ${update.duration}, ${update.volume}, ${update.comment}) returning id".query[Long].option

  }



  def insert(create: LogTaskRequest, projectId: Long, userId: Long) = {
    val start = create.startTime.withZoneSameInstant(ZoneOffset.UTC)
    val end: ZonedDateTime = start.plusMinutes(create.durationTime)

    fr"""insert into tb_task (
             project_id,
             user_id,
             create_time,
             task_description,
             start_time,
             end_time,
             duration,
             volume,
             comment
           ) VALUES (
             ${projectId},
             ${userId},
             ${ZonedDateTime.now(ZoneOffset.UTC).toLocalDateTime},
             ${create.taskDescription},
             ${start.toLocalDateTime},
             ${end.toLocalDateTime},
             ${create.durationTime},
             ${create.volume},
             ${create.comment}
           ) returning id"""
      .query[Long]
      .unique
  }

  def selectLastInsertedTask(id: Long) = {
    sql"select * from tb_task where id = ${id}".query[TaskTb].option
  }

  def deleteTask(taskDescription: String, projectId: Long, userId: Long) = {
    val created = ZonedDateTime.now(ZoneOffset.UTC).toLocalDateTime
    fr"""
          update tb_task set delete_time = ${created}, active = false
          where project_id = ${projectId} and
          user_id = ${userId}
          and task_description = ${taskDescription}
          and active = true
          """.update
  }

  def fetchTasksForProject(projectId: Int) = {
    fr"""
          select * from tb_task
          where project_id = ${projectId}
          and active = true
          """.query[TaskTb]
  }

  def fetchTask(taskDescription: String, userId: Long) = {
    sql"select * from tb_task where task_description = ${taskDescription} and user_id = ${userId} and active = true".query[TaskTb].option
  }

  def deleteTasksForProject(projectId: Long, deleteTime: ZonedDateTime): Update0 = {
    fr"""
        update tb_task set delete_time = ${deleteTime.toLocalDateTime}, active = false
        where project_id = ${projectId}
        """.update
  }

}
