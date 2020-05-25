package service

import java.time.{ZoneOffset, ZonedDateTime}

import cats.data.EitherT
import cats.effect.{ContextShift, IO}
import doobie.implicits._
import doobie.postgres.sqlstate
import doobie.util.transactor.Transactor.Aux
import data._
import error._
import data.Entities._

class TaskService(con: Aux[IO, Unit])(implicit val contextShift: ContextShift[IO]) {

  def logTask(task: LogTask): IO[Either[AppError, Task]] = (
    for {
      project <- findProjectById(task.projectName)
      userId <- getExistingUserId(task.userIdentification)
      id <- insertTask(task, project.id, userId)
      task <- getTaskById(id)
    } yield task
    ).value


  def deleteTask(deleteTaskRequest: DeleteTask): IO[Either[AppError, Int]] = (for {
    project <- findProjectById(deleteTaskRequest.projectName)
    userId <- getExistingUserId(deleteTaskRequest.userIdentification)
    updatedCount <- deleteTask(deleteTaskRequest.taskDescription, project.id, userId)
  } yield updatedCount).value

  def updateTask(updateTask: UpdateTask): IO[Either[AppError, Long]] = (
    for {
      userId <- getExistingUserId(updateTask.userIdentification)
      oldTask <- fetchTask(updateTask.oldTaskDescription, userId)
      _ <- deleteTask(oldTask.taskDescription, oldTask.projectId, oldTask.userId)
      updated <- updateExistingTask(newTask(oldTask, updateTask))
    } yield updated
    ).value

  private def newTask(oldTask: Task, updateTask: UpdateTask): UpdateTaskInsert = {

    val newStartTime = updateTask.startTime match {
      case Some(value) => value
      case None => ZonedDateTime.of(oldTask.startTime, ZoneOffset.UTC)
    }

    val newVolume: Option[Int] = updateTask.volume match {
      case Some(value: Int) => Some(value)
      case None => oldTask.volume match {
        case Some(value: Int) => Some(value)
        case None => None
      }
    }

    val comment = updateTask.comment match {
      case Some(value) => Some(value)
      case None => oldTask.comment match {
        case Some(value) => Some(value)
        case None => None
      }
    }
    UpdateTaskInsert(oldTask.projectId, oldTask.userId, updateTask.newTaskDescription, newStartTime, updateTask.durationTime, newVolume, comment)
  }

  private def updateExistingTask(toUpdate: UpdateTaskInsert): EitherT[IO, AppError, Long] = {
    EitherT.fromOptionF(Queries.Task.insertUpdate(toUpdate).transact(con), TaskUpdateUnsuccessful)
  }

  private def fetchTask(taskDescription: String, userId: Long): EitherT[IO, AppError, Task] = {
    EitherT.fromOptionF(Queries.Task.fetchTask(taskDescription, userId).transact(con), TaskWithGivenNameDoesNotExist)
  }


  private def deleteTask(taskDescription: String, projectId: Long, userId: Long): EitherT[IO, AppError, Int] = {
    EitherT(Queries.Task.deleteTask(taskDescription, projectId, userId).run.transact(con).attemptSomeSqlState {
      case sqlstate.class23.UNIQUE_VIOLATION => CannotChangeNameGivenTaskExistsAlready
    })
  }

  private def getTaskById(taskId: Long): EitherT[IO, AppError, Task] = {
    EitherT.fromOptionF(Queries.Task.selectLastInsertedTask(taskId).transact(con), CouldNotFindTheTask)
  }

  private def insertTask(task: LogTask, projectId: Long, userId: Long): EitherT[IO, AppError, Long] = {
    EitherT(Queries.Task.insert(task, projectId, userId).transact(con).attemptSomeSqlState {
      case sqlstate.class23.EXCLUSION_VIOLATION => CannotLogNewTaskWithTheOverlappingTimeRangeForTheSameUser
      case sqlstate.class23.UNIQUE_VIOLATION => CannotLogNewTaskWithDuplicateTaskDescriptionUnderTheSameProject
    })
  }

  private def findProjectById(projectName: String): EitherT[IO, AppError, Project] = {
    EitherT.fromOptionF(Queries.Project.getProject(projectName).transact(con), ProjectNotCreated)
  }

  private def getExistingUserId(uuid: String): EitherT[IO, AppError, Long] =
    EitherT.fromOptionF(Queries.User.getUserId(uuid).transact(con), UserNotFound)


}
