package service.project

import cats.data.EitherT
import cats.effect.Sync
import models.request.ChangeProjectNameRequest
import doobie.implicits._
import doobie.postgres.sqlstate
import errorMessages.{AppBusinessError, ProjectNameExists, ProjectNotCreated, UserNotFound}
import models.model.Project
import repository.project.{FindProjectByName, UpdateProjectName}
import repository.user.GetUserId


class ProjectUpdate[F[+_]: Sync](
                                  userId: GetUserId[F],
                                  updateProjectName: UpdateProjectName[F],
                                  findProject: FindProjectByName[F]) {


  def apply(project: ChangeProjectNameRequest, uuid: String): F[Either[AppBusinessError, Project]] = (for {
    userId <- getExistingUserId(uuid)
    _ <- changeProjectName(project.oldProjectName, project.projectName, userId)
    updatedRecord <- findProjectById(project.projectName)
  } yield updatedRecord).value


  private def getExistingUserId(userIdentification: String): EitherT[F, AppBusinessError, Int] = {
    EitherT.fromOptionF(userId(userIdentification), UserNotFound())
  }

  private def findProjectById(projectName: String): EitherT[F, AppBusinessError, Project] =
    EitherT(findProject(projectName))

  private def changeProjectName(oldProjectName: String, projectName: String, userId: Long): EitherT[F, AppBusinessError, Either[AppBusinessError, Unit]] =
    EitherT(updateProjectName(oldProjectName, projectName, userId).attemptSomeSqlState {
      case sqlstate.class23.UNIQUE_VIOLATION => ProjectNameExists()
    })

}
