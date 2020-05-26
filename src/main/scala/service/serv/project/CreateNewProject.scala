package service.serv.project

import cats.data.EitherT
import cats.effect.Sync
import data._
import error._
import service.repo.project.InsertProject
import service.repo.user.GetExistingUserId

class CreateNewProject[F[+_] : Sync](
                                      getUserId: GetExistingUserId[F],
                                      createProject: InsertProject[F]
                                    ) {

  def apply(project: CreateProject): F[Either[AppError, Long]] = {
    (for {
      userId <- getExistingUserId(project.userIdentification)
      projectId <- insertProject(project.projectName, userId)
    } yield projectId).value
  }

  private def getExistingUserId(userIdentification: String): EitherT[F, AppError, Long] = {
    EitherT.fromOptionF(getUserId(userIdentification), UserNotFound)
  }

  private def insertProject(projectName: String, userId: Long): EitherT[F, AppError, Long] = {
    EitherT(createProject(projectName, userId))
  }
}