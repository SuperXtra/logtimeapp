package service.task

import cats.data.EitherT
import cats.effect._
import models.request.DeleteTaskRequest
import error._
import models.model.Project
import repository.project.FindActiveProjectById
import repository.task.DeleteTask
import repository.user.GetExistingUserId

class TaskDelete[F[+_] : Sync](
                                getProjectId: FindActiveProjectById[F],
                                getUserId: GetExistingUserId[F],
                                delete: DeleteTask[F]) {


  def apply(deleteTaskRequest: DeleteTaskRequest, uuid: String): F[Either[AppError, Int]] = (for {
    project <- findProjectById(deleteTaskRequest.projectName)
    userId <- getExistingUserId(uuid)
    updatedCount <- deleteTask(deleteTaskRequest.taskDescription, project.id, userId)
  } yield updatedCount).value


  private def findProjectById(projectName: String): EitherT[F, AppError, Project] = {
    EitherT.fromOptionF(getProjectId(projectName), ProjectNotCreated)
  }

  private def getExistingUserId(uuid: String): EitherT[F, AppError, Int] =
    EitherT.fromOptionF(getUserId(uuid), UserNotFound())

  private def deleteTask(taskDescription: String, projectId: Long, userId: Long): EitherT[F, AppError, Int] = {
    EitherT(delete(taskDescription, projectId, userId))
  }

}
