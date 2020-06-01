package service.task

import cats.data.EitherT
import cats.effect._
import models.request.DeleteTaskRequest
import errorMessages._
import models.model.Project
import repository.project.FindActiveProjectById
import repository.task.DeleteTask
import repository.user.GetExistingUserId

class TaskDelete[F[+_] : Sync](
                                getProjectId: FindActiveProjectById[F],
                                getUserId: GetExistingUserId[F],
                                delete: DeleteTask[F]) {


  def apply(deleteTaskRequest: DeleteTaskRequest, uuid: String): F[Either[AppBusinessError, Int]] = (for {
    project <- findProjectById(deleteTaskRequest.projectName)
    userId <- getExistingUserId(uuid)
    updatedCount <- deleteTask(deleteTaskRequest.taskDescription, project.id, userId)
  } yield updatedCount).value


  private def findProjectById(projectName: String): EitherT[F, AppBusinessError, Project] = {
    EitherT.fromOptionF(getProjectId(projectName), ProjectNotCreated())
  }

  private def getExistingUserId(uuid: String): EitherT[F, AppBusinessError, Int] =
    EitherT.fromOptionF(getUserId(uuid), UserNotFound())

  private def deleteTask(taskDescription: String, projectId: Long, userId: Long): EitherT[F, AppBusinessError, Int] = {
    EitherT(delete(taskDescription, projectId, userId))
  }

}
