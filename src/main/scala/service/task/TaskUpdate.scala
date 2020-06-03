package service.task

import java.time.{ZoneOffset, ZonedDateTime}

import cats.data.EitherT
import cats.effect._
import models._
import models.model._
import models.request.UpdateTaskRequest
import errorMessages._
import repository.task._
import repository.user.GetUserId

class TaskUpdate[F[+_] : Sync](getUserId: GetUserId[F],
                               getUserTask: GetUserTask[F],
                               taskUpdate: UpdateTask[F]) {

  def apply(updateTask: UpdateTaskRequest, uuid: String): F[Either[AppBusinessError, Unit]] = (
    for {
      userId <- getExistingUserId(uuid)
      oldTask <- fetchTask(updateTask.oldTaskDescription, userId)
      updated <- updateExistingTask(newTask(oldTask, updateTask), oldTask.taskDescription, oldTask.projectId, userId)
    } yield updated
    ).value

  private def updateExistingTask(toUpdate: TaskToUpdate, taskDescription: String, projectId: Long, userId: Long): EitherT[F, AppBusinessError, Unit] = {
    EitherT(taskUpdate(toUpdate, ZonedDateTime.now(ZoneOffset.UTC).toLocalDateTime, taskDescription, projectId, userId))
  }

  private def fetchTask(taskDescription: String, userId: Long): EitherT[F, AppBusinessError, Task] = {
    EitherT.fromOptionF(getUserTask(taskDescription, userId), TaskNotFound())
  }

  private def getExistingUserId(uuid: String): EitherT[F, AppBusinessError, Int] =
    EitherT.fromOptionF(getUserId(uuid), UserNotFound())


  private def newTask(oldTask: Task, updateTask: UpdateTaskRequest): TaskToUpdate = {

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
    model.TaskToUpdate(oldTask.projectId, oldTask.userId, updateTask.newTaskDescription, newStartTime, updateTask.durationTime, newVolume, comment)
  }
}