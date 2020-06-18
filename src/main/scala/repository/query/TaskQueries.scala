package repository.query

import java.time._

import models.request.LogTaskRequest
import java.time.{ZoneOffset, ZonedDateTime}

import doobie._
import doobie.implicits._
import models.model._
import doobie.implicits.javatime._
import models.{ProjectId, TaskId, UserId}

object TaskQueries {

  def updateByInsert(update: TaskToUpdate, created: LocalDateTime) = {
    val start = update.startTime
    val end = start.plusMinutes(update.duration.value)
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
        ) returning id"""
      .query[Int]
  }

  def insert(create: LogTaskRequest, projectId: ProjectId, userId: UserId, startTime: LocalDateTime) = {
    val start = create.startTime.withZoneSameInstant(ZoneOffset.UTC)
    val end: ZonedDateTime = start.plusMinutes(create.durationTime.value)

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
             ${projectId.value},
             ${userId.value},
             ${startTime},
             ${create.taskDescription},
             ${start.toLocalDateTime},
             ${end.toLocalDateTime},
             ${create.durationTime.value},
             ${create.volume},
             ${create.comment}
           ) RETURNING id"""
      .query[Int]

  }

  def getTaskById(id: TaskId) = {
    sql"SELECT * FROM tb_task WHERE id = ${id.value}".query[Task]
  }

  def deleteTask(taskDescription: String, projectId: ProjectId, userId: UserId, deleteTime: LocalDateTime) = {
    fr"""
          UPDATE tb_task SET delete_time = ${deleteTime}, active = false
          WHERE project_id = ${projectId.value} AND
          user_id = ${userId.value}
          AND task_description = ${taskDescription}
          AND active = true
          """.update
  }

  def fetchTasksForProject(projectId: ProjectId) = {
    fr"""
          SELECT * FROM tb_task
          WHERE project_id = ${projectId.value}
          AND active = true
          """.query[Task]
  }

  def fetchTask(taskDescription: String, userId: UserId) = {
    sql"SELECT * FROM tb_task WHERE task_description = ${taskDescription} AND user_id = ${userId.value} AND active = true".query[Task]
  }

  def deleteTasksForProject(projectId: ProjectId, deleteTime: LocalDateTime): Update0 = {
    fr"""
        UPDATE tb_task SET delete_time = ${deleteTime}, active = false
        WHERE project_id = ${projectId.value}
        """.update
  }
}