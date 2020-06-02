package service.project

import java.time.{ZonedDateTime, _}

import cats.data.EitherT
import cats.effect._
import models.request.DeleteProjectRequest
import errorMessages._
import models.model.Project
import repository.project._
import repository.task.DeleteTasks
import repository.user.GetExistingUserId

class ProjectDeactivate[F[+_] : Sync](
                                       userId: GetExistingUserId[F],
                                       deactivateProject: DeleteProjectWithTasks[F],
                                       findProject: FindProjectByName[F],
                                       checkIfOwner: CheckIfIsProjectOwner[F]
                                     ) {

  def apply(projectRequest: DeleteProjectRequest, uuid: String): F[Either[AppBusinessError, Unit]] = {
    //TODO try to move delete time to for-comp
    (for {
      userId <- getUserId(uuid)
      deleteTime = ZonedDateTime.now(ZoneOffset.UTC)
      _ <- verifyIfUserIsTheOwnerOfTheProject(userId, projectRequest.projectName)
      project <- findProjectById(projectRequest.projectName)
      _ <- deleteProjectWithTasks(userId, projectRequest.projectName, project.id, deleteTime)
    } yield ()).value
  }

  private def getUserId(userIdentification: String): EitherT[F, AppBusinessError, Int] =
    EitherT.fromOptionF(userId(userIdentification), ProjectDeleteUnsuccessful())


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