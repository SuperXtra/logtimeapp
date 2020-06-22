package repository.query

import java.time._
import models.request.LogTaskRequest
import java.time.{ZoneOffset, ZonedDateTime}
import models.model._
import models._
import slick.jdbc.PostgresProfile.api._
import db.ColumnImplicits._
import db.TaskSchema
import slick.dbio.Effect
import slick.sql.FixedSqlAction

object TaskQueries {

  lazy val task = TaskSchema.tasks

  def updateByInsert(update: TaskToUpdate, created: LocalDateTime) = {
    val start = update.startTime
    val end = start.plusMinutes(update.duration.value)

    task returning task.map(_.id) +=
      Task(
        projectId = update.projectId,
        userId = update.userId ,
        createTime = created ,
        taskDescription = update.taskDescription ,
        startTime = start.toLocalDateTime,
        endTime = end.toLocalDateTime ,
        duration = update.duration,
        volume =  update.volume,
        comment = update.comment,
        deleteTime = None,
        active = Some(Active(true))
      )
  }

  def insert(create: LogTaskRequest, projectId: ProjectId, userId: UserId, startTime: LocalDateTime) = {
    val start = create.startTime.withZoneSameInstant(ZoneOffset.UTC)
    val end: ZonedDateTime = start.plusMinutes(create.durationTime.value)

    task returning task.map(_.id) +=
      Task(
        projectId = projectId,
        userId = userId ,
        createTime = startTime ,
        taskDescription = create.taskDescription ,
        startTime = start.toLocalDateTime,
        endTime = end.toLocalDateTime ,
        duration = create.durationTime ,
        volume =  create.volume,
        comment = create.comment,
        deleteTime = None,
        active = Some(Active(true))
      )
  }
  def deleteTask(taskDescription: String, projectId: ProjectId, userId: UserId, deleteTime: LocalDateTime) = {
    task.filter(t =>
      t.taskDescription === taskDescription &&
        t.projectId === projectId &&
        t.userId === userId &&
        t.active === Active(true)
    ).map(col =>
      (col.deleteTime, col.active))
      .update((Some(deleteTime), Some(Active(false))))
  }


  def deleteTasksForProject(projectId: ProjectId, deleteTime: LocalDateTime): FixedSqlAction[Int, NoStream, Effect.Write] = {
    task.filter(t => t.projectId === projectId)
      .map(col => (col.deleteTime, col.active))
      .update((Some(deleteTime), Some(Active(false))))
  }

  def getTaskById(id: TaskId) = {
    task.filter(t => t.id === id).result.headOption
  }

  def fetchTasksForProject(projectId: ProjectId) = {
    task.filter(t => t.projectId === projectId && t.active === Active(true)).result
  }

  def fetchTask(taskDescription: String, userId: UserId) = {
    task.filter(t => t.taskDescription === taskDescription && t.userId === userId && t.active === Active(true)).result.headOption
  }
}