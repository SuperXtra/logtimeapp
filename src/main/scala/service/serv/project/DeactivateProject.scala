package service.serv.project

import java.time.{ZonedDateTime, _}

import cats.data.EitherT
import cats.effect._
import data.DeleteProject
import data.Entities.Project
import error._
import service.refactor.repo.project.FindProjectById
import service.repo.project.{DeleteProjectR, FindProjectById}
import service.repo.task.DeleteTasks
import service.repo.user.GetExistingUserId

class DeactivateProject[F[+_] : Sync](
                                       getUserId: GetExistingUserId[F],
                                       deactivateProject: DeleteProjectR[F],
                                       findProject: FindProjectById[F],
                                       deactivateTasks: DeleteTasks[F]
                                     ) {

  def apply(project: DeleteProject): EitherT[F, AppError, Unit] = {
    //TODO try to move delete time to for-comp
    val deleteTime = ZonedDateTime.now(ZoneOffset.UTC)
    for {
      userId <- getExistingUserId(project.userIdentification)
      _ <- deleteProject(userId, project.projectName, deleteTime)
      projectId <- findProjectById(project.projectName)
      _ <- deleteTasksForProject(projectId.id, deleteTime)
    } yield ()
  }

  private def getExistingUserId(userIdentification: String): EitherT[F, AppError, Long] =
    EitherT.fromOptionF(getUserId(userIdentification), UserNotFound)

  private def deleteProject(userId: Long, projectName: String, deleteTime: ZonedDateTime): EitherT[F, AppError, Int] =
    EitherT(deactivateProject(userId, projectName, deleteTime))

  private def findProjectById(projectName: String): EitherT[F, AppError, Project] =
    EitherT.fromOptionF(findProject(projectName), ProjectNotCreated)

  private def deleteTasksForProject(id: Long, deleteTime: ZonedDateTime): EitherT[F, AppError, Int] =
    EitherT(deactivateTasks(id, deleteTime))
}
