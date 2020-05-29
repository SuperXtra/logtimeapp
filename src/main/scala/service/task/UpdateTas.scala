package service.task

import java.time.{ZoneOffset, ZonedDateTime}

import cats.data.EitherT
import cats.effect.{IO, Sync}
import models.{model, _}
import models.model.{TaskTb, TaskToUpdate}
import models.request.UpdateTaskRequest
import error._
import repository.task.{GetUserTask, TaskDelete, TaskInsertUpdate}
import repository.user.GetExistingUserId

class UpdateTas[F[+_] : Sync](getUserId: GetExistingUserId[F],
                              getUserTask: GetUserTask[F],
                              taskDelete: TaskDelete[F],
                              taskUpdate: TaskInsertUpdate[F]) {

  def apply(updateTask: UpdateTaskRequest, uuid: String): F[Either[AppError, Long]] = (
    for {
      userId <- getExistingUserId(uuid)
      oldTask <- fetchTask(updateTask.oldTaskDescription, userId)
      _ <- deleteTask(oldTask.taskDescription, oldTask.projectId, oldTask.userId)
      updated <- updateExistingTask(newTask(oldTask, updateTask))
    } yield updated
    ).value

  private def newTask(oldTask: TaskTb, updateTask: UpdateTaskRequest): TaskToUpdate = {

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



  private def updateExistingTask(toUpdate: TaskToUpdate): EitherT[F, AppError, Long] = {
    EitherT.fromOptionF(taskUpdate(toUpdate), TaskUpdateUnsuccessful())
  }

  private def deleteTask(taskDescription: String, projectId: Long, userId: Long): EitherT[F, AppError, Int] = {
    EitherT(taskDelete(taskDescription, projectId,userId))
  }

  private def fetchTask(taskDescription: String, userId: Long): EitherT[F, AppError, TaskTb] = {
    EitherT.fromOptionF(getUserTask(taskDescription, userId), TaskNotFound("Task with given name cannot be updated - it does not exist"))
  }

  private def getExistingUserId(uuid: String): EitherT[F, AppError, Int] =
    EitherT.fromOptionF(getUserId(uuid), UserNotFound())

}



