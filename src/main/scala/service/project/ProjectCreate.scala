package service.project

import cats.data.EitherT
import cats.effect.Sync
import models._
import models.request.CreateProjectRequest
import errorMessages._
import repository.project.InsertProject
import repository.user.GetExistingUserId

class ProjectCreate[F[+_] : Sync](
                                      getUserId: GetExistingUserId[F],
                                      createProject: InsertProject[F]
                                    ) {

  def apply(project: CreateProjectRequest, uuid: String): F[Either[AppBusinessError, Int]] = {
    (for {
      userId <- getExistingUserId(uuid)
      projectId <- insertProject(project.projectName, userId)
    } yield projectId).value
  }

  private def getExistingUserId(userIdentification: String): EitherT[F, AppBusinessError, Int] = {
    EitherT.fromOptionF(getUserId(userIdentification), UserNotFound())
  }

  private def insertProject(projectName: String, userId: Int): EitherT[F, AppBusinessError, Int] = {
    EitherT(createProject(projectName, userId))
  }
}