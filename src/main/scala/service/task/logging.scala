package service.task

import akka.event.MarkerLoggingAdapter
import cats.effect._
import cats.implicits._
import models.model.TaskToUpdate
import models.{ProjectId, TaskId, UserId}

object logging {


  def requestedTaskDeactivation[F[_]: Sync](taskDescription: String, projectName: String, uuid: String)
                                           (implicit logger: MarkerLoggingAdapter): Unit =
    logger.info(s"[SERVICE][TASK] Requested task deactivation with description: $taskDescription project name: $projectName by user with uuid: $uuid")

  def checkingWhetherProjectExists[F[_]: Sync](projectName: String)
                                              (implicit logger: MarkerLoggingAdapter): Unit =
    logger.info(s"[SERVICE][TASK] Checking if project with name $projectName exists")


  def checkingWhetherUserExists[F[_]: Sync](uuid: String)
                                           (implicit logger: MarkerLoggingAdapter): Unit =
    logger.info(s"[SERVICE][TASK] Checking whether user with uuid: $uuid exists")


  def deactivatingTask[F[_]: Sync](taskDescription: String, projectName: String, uuid: String)
                                  (implicit logger: MarkerLoggingAdapter): Unit =
    logger.info(s"[SERVICE][TASK] Deactivating task with name: $taskDescription project name: $projectName and owner with uuid: $uuid")


  def insertedTask[F[_]: Sync](taskId: TaskId)
                              (implicit logger: MarkerLoggingAdapter): Unit =
    logger.info(s"[SERVICE][TASK] Inserted task with id: ${taskId.value}")

  def oldTaskData(oldTaskDescription: String, userId: UserId)
                 (implicit logger: MarkerLoggingAdapter): Unit =
    logger.info(s"[SERVICE][TASK] Retrieving old task data with name ${oldTaskDescription} and user id: $userId")

  def updatedTaskData(update: TaskToUpdate, taskDescription: String, projectId: ProjectId, userId: UserId)
                     (implicit logger: MarkerLoggingAdapter): Unit =
    logger.info(s"[SERVICE][TASK] Task name $taskDescription changed successfully to ${update.taskDescription} for project ${projectId.value} and user id: ${userId.value}")

}