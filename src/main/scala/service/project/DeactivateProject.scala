package service.project

import java.time.{ZonedDateTime, _}

import cats.data.EitherT
import cats.effect._
import models.request.DeleteProjectRequest
import error._
import models.{IsOwner, ProjectId, UserId}
import models.model.Project
import repository.project._
import repository.task.DeleteTasks
import repository.user.GetUserByUUID

class DeactivateProject[F[+_] : Sync](
                                       userId: GetUserByUUID[F],
                                       deactivateProject: DeleteProjectWithTasks[F],
                                       findProject: GetProjectByName[F],
                                       checkIfOwner: IsProjectOwner[F]
                                     ) {

  def apply(projectName: String, uuid: String): F[Either[LogTimeAppError, Unit]] = {
    (for {
      userId <- getUserId(uuid)
      deleteTime = ZonedDateTime.now(ZoneOffset.UTC)
      project <- findProjectById(projectName)
      _ <- verifyIfUserIsTheOwnerOfTheProject(userId, projectName)
      _ <- deleteProjectWithTasks(userId, projectName, project.id, deleteTime)
    } yield ()).value
  }

  private def getUserId(userIdentification: String): EitherT[F, UserNotFound.type, UserId] =
    EitherT.fromOptionF(userId(userIdentification), UserNotFound )


  private def findProjectById(projectName: String): EitherT[F, LogTimeAppError, Project] = {
    EitherT.fromOptionF(findProject(projectName), ProjectNotFound)
  }

  private def verifyIfUserIsTheOwnerOfTheProject(userId: UserId, projectName: String): EitherT[F, LogTimeAppError, IsOwner] = {
    EitherT(checkIfOwner(userId, projectName))
  }

  private def deleteProjectWithTasks(userId: UserId, projectName: String, projectId: ProjectId, deleteTime: ZonedDateTime): EitherT[F, LogTimeAppError, Unit] = {
    EitherT(deactivateProject(userId, projectName, projectId, deleteTime.toLocalDateTime))
  }
}