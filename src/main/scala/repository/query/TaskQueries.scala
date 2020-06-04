package repository.query

import java.time._

import models.request.LogTaskRequest
import java.time.{ZoneOffset, ZonedDateTime}
import doobie._
import doobie.implicits._
import models.model._
import doobie.implicits.javatime._

object TaskQueries {

  def updateByInsert(update: TaskToUpdate, created: LocalDateTime) = {
    val start = update.startTime
    val end = start.plusMinutes(update.duration)
    fr"""INSERT INTO tb_task (
        project_id,
        user_id,
        create_time,
        task_description,
        start_time,
        end_time,
        duration,
        volume,
        comment) VALUES (
        ${update.projectId},
        ${update.userId},
        ${created},
        ${update.taskDescription},
        ${start.toLocalDateTime},
        ${end.toLocalDateTime},
        ${update.duration},
        ${update.volume},
        ${update.comment}
        ) RETURNING id"""
      .query[Long]
  }

  def insert(create: LogTaskRequest, projectId: Long, userId: Long, startTime: LocalDateTime) = {
    val start = create.startTime.withZoneSameInstant(ZoneOffset.UTC)
    val end: ZonedDateTime = start.plusMinutes(create.durationTime)

    fr"""INSERT INTO tb_task (
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
             ${projectId.toInt},
             ${userId.toInt},
             ${startTime},
             ${create.taskDescription},
             ${start.toLocalDateTime},
             ${end.toLocalDateTime},
             ${create.durationTime.toInt},
             ${create.volume},
             ${create.comment}
           ) RETURNING id"""
      .query[Int]

  }

  def getTaskById(id: Long) = {
    sql"SELECT * FROM tb_task WHERE id = ${id}".query[Task]
  }

  def deleteTask(taskDescription: String, projectId: Long, userId: Long, deleteTime: LocalDateTime) = {
    fr"""
          UPDATE tb_task SET delete_time = ${deleteTime}, active = false
          WHERE project_id = ${projectId.toInt} AND
          user_id = ${userId.toInt}
          AND task_description = ${taskDescription}
          AND active = true
          """.update
  }

  def fetchTasksForProject(projectId: Int) = {
    fr"""
          SELECT * FROM tb_task
          WHERE project_id = ${projectId}
          AND active = true
          """.query[Task]
  }

  def fetchTask(taskDescription: String, userId: Long) = {
    sql"SELECT * FROM tb_task WHERE task_description = ${taskDescription} AND user_id = ${userId} AND active = true".query[Task]
  }

  def deleteTasksForProject(projectId: Long, deleteTime: LocalDateTime): Update0 = {
    fr"""
        UPDATE tb_task SET delete_time = ${deleteTime}, active = false
        WHERE project_id = ${projectId}
        """.update
  }
}