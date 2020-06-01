package service.task

import cats.data.EitherT
import cats.effect.Sync
import models.request.LogTaskRequest
import errorMessages.{AppBusinessError, ProjectNotFound, TaskNotCreated, UserNotFound}
import models.model.{Project, Task}
import repository.project.FindActiveProjectById
import repository.task.{GetTask, InsertTask}
import repository.user.GetExistingUserId


class TaskLog[F[+_]: Sync](
                            getProjectId: FindActiveProjectById[F],
                            getUserId: GetExistingUserId[F],
                            insertTask: InsertTask[F],
                            getTask: GetTask[F]) {

  def apply(work: LogTaskRequest, uuid: String): F[Either[AppBusinessError, Task]] = {
    (for {
      projectId <- getExistingProjectId(work.projectName)
      userId <- getExistingUserId(uuid)
      taskId <- insertNewTask(work, projectId.id, userId)
      task <- getExistingTask(taskId)
    } yield task).value
  }

  private def getExistingProjectId(projectName: String): EitherT[F, AppBusinessError, Project] =
    EitherT(getProjectId(projectName))

  private def getExistingUserId(userIdentification: String): EitherT[F, AppBusinessError, Int] =
    EitherT.fromOptionF(getUserId(userIdentification), UserNotFound())

  private def insertNewTask(work: LogTaskRequest, projectId: Long, userId: Long): EitherT[F, AppBusinessError, Int] =
    EitherT(insertTask(work, projectId, userId))

  private def getExistingTask(taskId: Long): EitherT[F, AppBusinessError, Task] =
    EitherT.fromOptionF(getTask(taskId), TaskNotCreated())
}
