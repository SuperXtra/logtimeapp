package service.task

import cats.data.EitherT
import cats.effect.{IO, Sync}
import models.request.DeleteTaskRequest
import doobie.postgres.sqlstate
import error.{AppError, CannotChangeNameGivenTaskExistsAlready, ProjectNotCreated, UserNotFound}
import models.model.ProjectTb
import repository.project.FindProjectById
import repository.task.TaskDelete
import repository.user.GetExistingUserId

class DeleteTas[F[+_] : Sync](
                               getProjectId: FindProjectById[F],
                               getUserId: GetExistingUserId[F],
                               delete: TaskDelete[F]) {


  def apply(deleteTaskRequest: DeleteTaskRequest): F[Either[AppError, Int]] = (for {
    project <- findProjectById(deleteTaskRequest.projectName)
    userId <- getExistingUserId(deleteTaskRequest.userIdentification)
    updatedCount <- deleteTask(deleteTaskRequest.taskDescription, project.id, userId)
  } yield updatedCount).value


  private def findProjectById(projectName: String): EitherT[F, AppError, ProjectTb] = {
    EitherT.fromOptionF(getProjectId(projectName), ProjectNotCreated)
  }

  private def getExistingUserId(uuid: String): EitherT[F, AppError, Long] =
    EitherT.fromOptionF(getUserId(uuid), UserNotFound)

  private def deleteTask(taskDescription: String, projectId: Long, userId: Long): EitherT[F, AppError, Int] = {
    EitherT(delete(taskDescription, projectId, userId))
  }

}
