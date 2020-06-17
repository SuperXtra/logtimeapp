package service.task

import java.time.{ZoneOffset, ZonedDateTime}

import cats.data.EitherT
import cats.effect._
import models.request.DeleteTaskRequest
import error._
import models.model.Project
import repository.project.GetProjectByName
import repository.task.DeleteTask
import repository.user.GetUserByUUID

class DeactivateTask[F[+_] : Sync](
                                getProjectId: GetProjectByName[F],
                                getUserId: GetUserByUUID[F],
                                delete: DeleteTask[F]) {


  def apply(taskDescription: String, projectName: String, uuid: String): F[Either[LogTimeAppError, Int]] = (for {
    project <- findProjectById(projectName)
    userId <- getExistingUserId(uuid)
    updatedCount <- deleteTask(taskDescription, project.id, userId)
  } yield updatedCount).value


  private def findProjectById(projectName: String): EitherT[F, LogTimeAppError, Project] = {
    EitherT.fromOptionF(getProjectId(projectName), ProjectNotFound)
  }

  private def getExistingUserId(uuid: String): EitherT[F, LogTimeAppError, Int] =
    EitherT.fromOptionF(getUserId(uuid), UserNotFound )

  private def deleteTask(taskDescription: String, projectId: Long, userId: Long): EitherT[F, LogTimeAppError, Int] =
    EitherT(delete(taskDescription, projectId, userId, ZonedDateTime.now(ZoneOffset.UTC).toLocalDateTime))
}
