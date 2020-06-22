package service.task

import java.time.{ZoneOffset, ZonedDateTime}

import akka.event.MarkerLoggingAdapter
import cats.effect._
import db.DatabaseContext
import models._
import models.model._
import models.request.UpdateTaskRequest
import error._
import repository.task._
import repository.user.GetUserByUUID
import slick.jdbc.PostgresProfile.api._
import utils.EitherT
import scala.concurrent._
import ExecutionContext.Implicits.global
import db.RunDBIOAction._

class UpdateTask[F[+_] : Sync](getUserId: GetUserByUUID[F],
                               getUserTask: GetUserTask[F],
                               taskUpdate: ChangeTask[F])
                              (implicit db: Database,
                               logger: MarkerLoggingAdapter,
                               ec: ContextShift[IO])  {

  def apply(updateTask: UpdateTaskRequest, uuid: String) =
    (for {
        user <- getExistingUserId(uuid)
        _ = logging.checkingWhetherUserExists(uuid)
        userId = user.userId
        oldTask <- fetchTask(updateTask.oldTaskDescription, userId)
        _ = logging.oldTaskData(updateTask.oldTaskDescription, userId)
        _ <- updateExistingTask(newTask(oldTask, updateTask), oldTask.taskDescription, oldTask.projectId, userId)
        _ = logging.updatedTaskData(newTask(oldTask, updateTask), oldTask.taskDescription, oldTask.projectId, userId)
      } yield ()).value.transactionally.exec

  private def updateExistingTask(toUpdate: TaskToUpdate, taskDescription: String, projectId: ProjectId, userId: UserId) = {
    EitherT(taskUpdate(toUpdate, ZonedDateTime.now(ZoneOffset.UTC).toLocalDateTime, taskDescription, projectId, userId))
  }

  private def fetchTask(taskDescription: String, userId: UserId) = {
    EitherT(getUserTask(taskDescription, userId))
  }

  private def getExistingUserId(uuid: String) =
    EitherT(getUserId(uuid))

  private def newTask(oldTask: Task, updateTask: UpdateTaskRequest): TaskToUpdate = {

    val newStartTime = updateTask.startTime.getOrElse(ZonedDateTime.of(oldTask.startTime, ZoneOffset.UTC))
    val newVolume: Option[Volume] = updateTask.volume.orElse(oldTask.volume)
    val comment = updateTask.comment.orElse(oldTask.comment)

    model.TaskToUpdate(oldTask.projectId, oldTask.userId, updateTask.newTaskDescription, newStartTime, updateTask.durationTime, newVolume, comment)
  }
}