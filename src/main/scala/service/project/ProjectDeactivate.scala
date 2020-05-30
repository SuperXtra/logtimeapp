package service.project

import java.time.{ZonedDateTime, _}

import cats.data.EitherT
import cats.effect._
import models.request.DeleteProjectRequest
import error._
import models.model.Project
import repository.project.{DeleteProjectR, FindProjectById}
import repository.task.DeleteTasks
import repository.user.GetExistingUserId

class ProjectDeactivate[F[+_] : Sync](
                                       getUserId: GetExistingUserId[F],
                                       deactivateProject: DeleteProjectR[F],
                                       findProject: FindProjectById[F],
                                       deactivateTasks: DeleteTasks[F]
                                     ) {

  def apply(project: DeleteProjectRequest, uuid: String): F[Either[AppError, Unit]] = {
    //TODO try to move delete time to for-comp
    val deleteTime = ZonedDateTime.now(ZoneOffset.UTC)
    (for {
      userId <- getExistingUserId(uuid)
      _ <- deleteProject(userId, project.projectName, deleteTime)
      projectId <- findProjectById(project.projectName)
      _ <- deleteTasksForProject(projectId.id, deleteTime)
    } yield ()).value
  }

  private def getExistingUserId(userIdentification: String): EitherT[F, AppError, Int] =
    EitherT.fromOptionF(getUserId(userIdentification), ProjectDeleteUnsuccessful("Given user uuid does not exists or is not the owner ow the project"))

  private def deleteProject(userId: Long, projectName: String, deleteTime: ZonedDateTime): EitherT[F, AppError, Int] =
    EitherT(deactivateProject(userId, projectName, deleteTime))

  private def findProjectById(projectName: String): EitherT[F, AppError, Project] =
    EitherT.fromOptionF(findProject(projectName), ProjectNotCreated())

  private def deleteTasksForProject(id: Long, deleteTime: ZonedDateTime): EitherT[F, AppError, Int] =
    EitherT(deactivateTasks(id, deleteTime))
}
