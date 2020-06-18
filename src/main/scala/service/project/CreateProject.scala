package service.project

import cats.data.EitherT
import cats.effect.Sync
import models._
import models.request.CreateProjectRequest
import error._
import repository.project.InsertProject
import repository.user.GetUserByUUID

class CreateProject[F[+_] : Sync](
                                   getUserId: GetUserByUUID[F],
                                   createProject: InsertProject[F]
                                    ) {

  def apply(projectName: String, uuid: String): F[Either[LogTimeAppError, ProjectId]] = {
    (for {
      userId <- getExistingUserId(uuid)
      projectId <- insertProject(projectName, userId)
    } yield projectId).value
  }

  private def getExistingUserId(userIdentification: String): EitherT[F, UserNotFound.type, UserId] = {
    EitherT.fromOptionF(getUserId(userIdentification), UserNotFound )
  }

  private def insertProject(projectName: String, userId: UserId): EitherT[F, LogTimeAppError, ProjectId] = {
    EitherT(createProject(projectName, userId))
  }
}