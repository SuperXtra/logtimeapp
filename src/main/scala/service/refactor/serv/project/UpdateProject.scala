package service.refactor.serv.project

import cats.data.EitherT
import cats.effect.Sync
import data.ChangeProjectName
import data.Entities.Project
import doobie.implicits._
import doobie.postgres.sqlstate
import doobie.util.transactor.Transactor
import error.{AppError, CannotChangeNameGivenProjectNameExistsAlready, ProjectNotCreated, UserNotFound}
import service.refactor.repo.user.GetExistingUserId
import service.refactor.repo.project.{FindProjectById, UpdateProjectName}


class UpdateProject[F[+_]: Sync](
                                userId: GetExistingUserId[F],
                                updateProjectName: UpdateProjectName[F],
                                findProject: FindProjectById[F]) {


  def apply(project: ChangeProjectName): F[Either[AppError, Project]] = (for {
    userId <- getExistingUserId(project.userIdentification)
    _ <- changeProjectName(project.oldProjectName, project.projectName, userId)
    updatedRecord <- findProjectById(project.projectName)
  } yield updatedRecord).value


  private def getExistingUserId(userIdentification: String): EitherT[F, AppError, Long] = {
    EitherT.fromOptionF(userId(userIdentification), UserNotFound)
  }

  private def findProjectById(projectName: String): EitherT[F, AppError, Project] =
    EitherT.fromOptionF(findProject(projectName), ProjectNotCreated)

  private def changeProjectName(oldProjectName: String, projectName: String, userId: Long): EitherT[F, AppError, Int] =
    EitherT(updateProjectName(oldProjectName, projectName, userId).attemptSomeSqlState {
      case sqlstate.class23.UNIQUE_VIOLATION => CannotChangeNameGivenProjectNameExistsAlready
    })

}
