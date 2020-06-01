package service.task

import java.time.{ZoneOffset, ZonedDateTime}

import cats.data.EitherT
import cats.effect.{IO, Sync}
import models.{model, _}
import models.model.{Task, TaskToUpdate}
import models.request.UpdateTaskRequest
import errorMessages._
import repository.task.{GetUserTask, DeleteTask, TaskInsertUpdate}
import repository.user.GetExistingUserId

class TaskUpdate[F[+_] : Sync](getUserId: GetExistingUserId[F],
                               getUserTask: GetUserTask[F],
                               taskDelete: DeleteTask[F],
                               taskUpdate: TaskInsertUpdate[F]) {

  def apply(updateTask: UpdateTaskRequest, uuid: String): F[Either[AppBusinessError, Unit]] = (
    for {
      userId <- getExistingUserId(uuid)
      oldTask <- fetchTask(updateTask.oldTaskDescription, userId)
      _ <- deleteTask(oldTask.taskDescription, oldTask.projectId, oldTask.userId)
      updated <- updateExistingTask(newTask(oldTask, updateTask))
    } yield updated
    ).value

  private def newTask(oldTask: Task, updateTask: UpdateTaskRequest): TaskToUpdate = {

    // TODO beautify

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



  private def updateExistingTask(toUpdate: TaskToUpdate): EitherT[F, AppBusinessError, Unit] = {
    EitherT(taskUpdate(toUpdate, ZonedDateTime.now(ZoneOffset.UTC).toLocalDateTime))
  }

  private def deleteTask(taskDescription: String, projectId: Long, userId: Long): EitherT[F, AppBusinessError, Int] = {
    EitherT(taskDelete(taskDescription, projectId,userId, ZonedDateTime.now(ZoneOffset.UTC).toLocalDateTime))
  }

  private def fetchTask(taskDescription: String, userId: Long): EitherT[F, AppBusinessError, Task] = {
    EitherT.fromOptionF(getUserTask(taskDescription, userId), TaskNotFound())
  }

  private def getExistingUserId(uuid: String): EitherT[F, AppBusinessError, Int] =
    EitherT.fromOptionF(getUserId(uuid), UserNotFound())

}



