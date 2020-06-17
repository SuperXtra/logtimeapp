package service.task

import java.time.{ZoneOffset, ZonedDateTime}

import cats.data.EitherT
import cats.effect._
import models._
import models.model._
import models.request.UpdateTaskRequest
import error._
import repository.task._
import repository.user.GetUserByUUID

class UpdateTask[F[+_] : Sync](getUserId: GetUserByUUID[F],
                               getUserTask: GetUserTask[F],
                               taskUpdate: ChangeTask[F]) {

  def apply(updateTask: UpdateTaskRequest, uuid: String): F[Either[LogTimeAppError, Unit]] = (
    for {
      userId <- getExistingUserId(uuid)
      oldTask <- fetchTask(updateTask.oldTaskDescription, userId)
      _ <- updateExistingTask(newTask(oldTask, updateTask), oldTask.taskDescription, oldTask.projectId, userId)
    } yield ()).value

  private def updateExistingTask(toUpdate: TaskToUpdate, taskDescription: String, projectId: Long, userId: Long): EitherT[F, LogTimeAppError, Unit] = {
    EitherT(taskUpdate(toUpdate, ZonedDateTime.now(ZoneOffset.UTC).toLocalDateTime, taskDescription, projectId, userId))
  }

  private def fetchTask(taskDescription: String, userId: Long): EitherT[F, LogTimeAppError, Task] = {
    EitherT.fromOptionF(getUserTask(taskDescription, userId), TaskNotFound )
  }

  private def getExistingUserId(uuid: String): EitherT[F, LogTimeAppError, Int] =
    EitherT.fromOptionF(getUserId(uuid), UserNotFound )

  private def newTask(oldTask: Task, updateTask: UpdateTaskRequest): TaskToUpdate = {

    val newStartTime = updateTask.startTime.getOrElse(ZonedDateTime.of(oldTask.startTime, ZoneOffset.UTC))
    val newVolume: Option[Int] = updateTask.volume.orElse(oldTask.volume)
    val comment = updateTask.comment.orElse(oldTask.comment)

    model.TaskToUpdate(oldTask.projectId, oldTask.userId, updateTask.newTaskDescription, newStartTime, updateTask.durationTime, newVolume, comment)
  }
}