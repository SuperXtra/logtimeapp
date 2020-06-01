package service.project

import cats.data.EitherT
import cats.effect.Sync
import models.request.ChangeProjectNameRequest
import doobie.implicits._
import doobie.postgres.sqlstate
import error.{AppError, ProjectNameExists, ProjectNotCreated, UserNotFound}
import models.model.Project
import repository.project.{FindActiveProjectById, UpdateProjectName}
import repository.user.GetExistingUserId


class ProjectUpdate[F[+_]: Sync](
                                userId: GetExistingUserId[F],
                                updateProjectName: UpdateProjectName[F],
                                findProject: FindActiveProjectById[F]) {


  def apply(project: ChangeProjectNameRequest, uuid: String): F[Either[AppError, Project]] = (for {
    userId <- getExistingUserId(uuid)
    _ <- changeProjectName(project.oldProjectName, project.projectName, userId)
    updatedRecord <- findProjectById(project.projectName)
  } yield updatedRecord).value


  private def getExistingUserId(userIdentification: String): EitherT[F, AppError, Int] = {
    EitherT.fromOptionF(userId(userIdentification), UserNotFound())
  }

  private def findProjectById(projectName: String): EitherT[F, AppError, Project] =
    EitherT.fromOptionF(findProject(projectName), ProjectNotCreated())

  private def changeProjectName(oldProjectName: String, projectName: String, userId: Long): EitherT[F, AppError, Int] =
    EitherT(updateProjectName(oldProjectName, projectName, userId).attemptSomeSqlState {
      case sqlstate.class23.UNIQUE_VIOLATION => ProjectNameExists()
    })

}
