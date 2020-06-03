package service.task

import java.time.{ZoneOffset, ZonedDateTime}

import cats.data.EitherT
import cats.effect.Sync
import models.request.LogTaskRequest
import errorMessages.{AppBusinessError, ProjectNotFound, TaskNotCreated, UserNotFound}
import models.model.{Project, Task}
import repository.project.FindProjectByName
import repository.task.{GetTask, CreateTask}
import repository.user.GetUserId


class TaskLog[F[+_]: Sync](
                            getProjectId: FindProjectByName[F],
                            getUserId: GetUserId[F],
                            insertTask: CreateTask[F],
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
    EitherT(insertTask(work, projectId, userId, ZonedDateTime.now(ZoneOffset.UTC).toLocalDateTime))

  private def getExistingTask(taskId: Long): EitherT[F, AppBusinessError, Task] =
    EitherT.fromOptionF(getTask(taskId), TaskNotCreated())
}
