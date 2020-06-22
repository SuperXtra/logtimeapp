package service.task

import java.time.{ZoneOffset, ZonedDateTime}

import akka.event.MarkerLoggingAdapter
import cats.effect._
import models._
import repository.project.GetProjectByName
import repository.task.DeleteTask
import repository.user.GetUserByUUID
import slick.jdbc.PostgresProfile.api._
import utils.EitherT

import scala.concurrent._
import ExecutionContext.Implicits.global
import db.RunDBIOAction._
import error.LogTimeAppError

class DeactivateTask[F[+_] : Sync](
                                    getProjectId: GetProjectByName[F],
                                    getUserId: GetUserByUUID[F],
                                    delete: DeleteTask[F])
                                  (implicit db: Database,
                                   logger: MarkerLoggingAdapter,
                                   ec: ContextShift[IO])  {


  def apply(taskDescription: String, projectName: String, uuid: String): IO[Either[LogTimeAppError, DeleteCount]] = {
    logging.requestedTaskDeactivation(taskDescription: String, projectName: String, uuid: String)
    (for {
    project <- findProjectById(projectName)
    _ = logging.checkingWhetherProjectExists(projectName: String)
    userId <- getExistingUserId(uuid)
    _ = logging.checkingWhetherUserExists(uuid: String)
    updatedCount <- deleteTask(taskDescription, project.id, userId.userId)
      _ = logging.deactivatingTask(taskDescription: String, projectName: String, uuid: String)
  } yield updatedCount).value.transactionally.exec
  }

  private def findProjectById(projectName: String) =
    EitherT(getProjectId(projectName))

  private def getExistingUserId(uuid: String) =
    EitherT(getUserId(uuid))

  private def deleteTask(taskDescription: String, projectId: ProjectId, userId: UserId) =
    EitherT(delete(taskDescription, projectId, userId, ZonedDateTime.now(ZoneOffset.UTC).toLocalDateTime))
}

