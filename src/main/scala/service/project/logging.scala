package service.project

import java.time.ZonedDateTime

import akka.event.MarkerLoggingAdapter
import cats.effect._
import cats.implicits._
import models.{IsOwner, ProjectId, UserId}

object logging {
  def changedProjectName[F[_]: Sync](oldName: String, newProjectName: String)
                                    (implicit logger: MarkerLoggingAdapter): Unit =
    logger.info(s"[SERVICE][PROJECT] Changed project name from $oldName to $newProjectName")


  def projectDeactivationSuccessful[F[_]: Sync](userId: UserId, projectName: String, id: ProjectId, deleteTime: ZonedDateTime)
                                               (implicit logger: MarkerLoggingAdapter): Unit =
    logger.info(s"[SERVICE][PROJECT] User with id: ${userId.value} deactivated project: ${projectName} with id: ${id.value} at time: ${deleteTime}")

  def userIsOwner[F[_]: Sync](verification: IsOwner, userId: UserId)
                             (implicit logger: MarkerLoggingAdapter): Unit =
    verification match {
      case IsOwner(value) if value =>
        logger.info(s"[SERVICE][PROJECT] User with id: ${userId.value} may deactivate project")
      case IsOwner(value) if !value =>
        logger.warning(s"[SERVICE][PROJECT] User with id: ${userId.value} may not deactivate project")
    }

  def projectCreated[F[_]: Sync](projectId: ProjectId, projectName: String, userId: UserId)
                                (implicit logger: MarkerLoggingAdapter): Unit =
      logger.info(s"[SERVICE][PROJECT] User with id: ${userId.value} created new project with id: ${projectId.value} and name: ${projectName}")

  def foundUserWithId[F[_]: Sync](userId: UserId, uuid: String)
                                (implicit logger: MarkerLoggingAdapter): Unit =
    logger.info(s"[SERVICE][PROJECT] Found user with id: ${userId.value} for UUID: $uuid")

  def foundProjectByName[F[_]: Sync](projectName: String)
                                    (implicit logger: MarkerLoggingAdapter): Unit =
    logger.info(s"[SERVICE][PROJECT] Found project with name: $projectName")
}