package service.project

import java.time.{ZonedDateTime, _}

import cats.data.EitherT
import cats.effect._
import models.request.DeleteProjectRequest
import errorMessages._
import models.model.Project
import repository.project._
import repository.task.DeleteTasks
import repository.user.GetUserId

class ProjectDeactivate[F[+_] : Sync](
                                       userId: GetUserId[F],
                                       deactivateProject: DeleteProjectWithTasks[F],
                                       findProject: FindProjectByName[F],
                                       checkIfOwner: IsProjectOwner[F]
                                     ) {

  def apply(projectRequest: DeleteProjectRequest, uuid: String): F[Either[AppBusinessError, Unit]] = {
    (for {
      userId <- getUserId(uuid)
      deleteTime = ZonedDateTime.now(ZoneOffset.UTC)
      project <- findProjectById(projectRequest.projectName)
      _ <- verifyIfUserIsTheOwnerOfTheProject(userId, projectRequest.projectName)
      _ <- deleteProjectWithTasks(userId, projectRequest.projectName, project.id, deleteTime)
    } yield ()).value
  }

  private def getUserId(userIdentification: String): EitherT[F, AppBusinessError, Int] =
    EitherT.fromOptionF(userId(userIdentification), UserNotFound())


  private def findProjectById(projectName: String): EitherT[F, AppBusinessError, Project] = {
    EitherT(findProject(projectName))
  }

  private def verifyIfUserIsTheOwnerOfTheProject(userId: Int, projectName: String): EitherT[F, AppBusinessError, Boolean] = {
    EitherT(checkIfOwner(userId, projectName))
  }

  private def deleteProjectWithTasks(userId: Int, projectName: String, projectId: Int, deleteTime: ZonedDateTime): EitherT[F, AppBusinessError, Unit] = {
    EitherT(deactivateProject(userId, projectName, projectId, deleteTime.toLocalDateTime))
  }
}