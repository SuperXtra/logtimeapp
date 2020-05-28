package service.project

import cats.data.EitherT
import cats.effect.Sync
import models._
import models.request.CreateProjectRequest
import error._
import repository.project.InsertProject
import repository.user.GetExistingUserId

class CreateNewProject[F[+_] : Sync](
                                      getUserId: GetExistingUserId[F],
                                      createProject: InsertProject[F]
                                    ) {

  def apply(project: CreateProjectRequest): F[Either[AppError, Long]] = {
    (for {
      userId <- getExistingUserId(project.userIdentification)
      projectId <- insertProject(project.projectName, userId)
    } yield projectId).value
  }

  private def getExistingUserId(userIdentification: String): EitherT[F, AppError, Long] = {
    EitherT.fromOptionF(getUserId(userIdentification), UserNotFound())
  }

  private def insertProject(projectName: String, userId: Long): EitherT[F, AppError, Long] = {
    EitherT(createProject(projectName, userId))
  }
}