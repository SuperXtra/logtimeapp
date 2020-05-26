package service.refactor.serv.task

import java.time.{ZoneOffset, ZonedDateTime}

import cats.data.EitherT
import cats.effect.{IO, Sync}
import data.Entities.Task
import data._
import error._
import service.refactor.repo.task._
import service.refactor.repo.user._

class UpdateTas[F[+_] : Sync](getUserId: GetExistingUserId[F],
                              getUserTask: GetUserTask[F],
                              taskDelete: TaskDelete[F],
                              taskUpdate: TaskInsertUpdate[F]) {

  def apply(updateTask: UpdateTask): F[Either[AppError, Long]] = (
    for {
      userId <- getExistingUserId(updateTask.userIdentification)
      oldTask <- fetchTask(updateTask.oldTaskDescription, userId)
      _ <- deleteTask(oldTask.taskDescription, oldTask.projectId, oldTask.userId)
      updated <- updateExistingTask(newTask(oldTask, updateTask))
    } yield updated
    ).value

  private def newTask(oldTask: Task, updateTask: UpdateTask): UpdateTaskInsert = {

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
    UpdateTaskInsert(oldTask.projectId, oldTask.userId, updateTask.newTaskDescription, newStartTime, updateTask.durationTime, newVolume, comment)
  }



  private def updateExistingTask(toUpdate: UpdateTaskInsert): EitherT[F, AppError, Long] = {
    EitherT.fromOptionF(taskUpdate(toUpdate), TaskUpdateUnsuccessful)
  }

  private def deleteTask(taskDescription: String, projectId: Long, userId: Long): EitherT[F, AppError, Int] = {
    EitherT(taskDelete(taskDescription, projectId,userId))
  }

  private def fetchTask(taskDescription: String, userId: Long): EitherT[F, AppError, Task] = {
    EitherT.fromOptionF(getUserTask(taskDescription, userId), TaskWithGivenNameDoesNotExist)
  }

  private def getExistingUserId(uuid: String): EitherT[F, AppError, Long] =
    EitherT.fromOptionF(getUserId(uuid), UserNotFound)

}



