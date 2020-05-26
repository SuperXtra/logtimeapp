package service.task

import cats.data.EitherT
import cats.effect.Sync
import models.request.LogTaskRequest
import error.{AppError, ProjectNotFound, TaskNotCreated, UserNotFound}
import models.model.{ProjectTb, TaskTb}
import repository.project.FindProjectById
import repository.task.{GetTask, InsertTask}
import repository.user.GetExistingUserId


class LogTask[F[+_]: Sync](
                            getProjectId: FindProjectById[F],
                            getUserId: GetExistingUserId[F],
                            insertTask: InsertTask[F],
                            getTask: GetTask[F]) {

  def apply(work: LogTaskRequest): F[Either[AppError, TaskTb]] = {
    (for {
      projectId <- getExistingProjectId(work.projectName)
      userId <- getExistingUserId(work.userIdentification)
      taskId <- insertNewTask(work, projectId.id, userId)
      task <- getExistingTask(taskId)
    } yield task).value
  }

  private def getExistingProjectId(projectName: String): EitherT[F, AppError, ProjectTb] =
    EitherT.fromOptionF(getProjectId(projectName), ProjectNotFound)

  private def getExistingUserId(userIdentification: String): EitherT[F, AppError, Long] =
    EitherT.fromOptionF(getUserId(userIdentification), UserNotFound)

  private def insertNewTask(work: LogTaskRequest, projectId: Long, userId: Long): EitherT[F, AppError, Long] =
    EitherT(insertTask(work, projectId, userId))

  private def getExistingTask(taskId: Long): EitherT[F, AppError, TaskTb] =
    EitherT.fromOptionF(getTask(taskId), TaskNotCreated)
}
