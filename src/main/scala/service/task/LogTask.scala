package service.task

import java.time.{ZoneOffset, ZonedDateTime}

import cats.data.EitherT
import cats.effect.Sync
import models.request.LogTaskRequest
import error.{LogTimeAppError, ProjectNotFound, TaskNotCreated, UserNotFound}
import models.model.{Project, Task}
import repository.project.GetProjectByName
import repository.task.{GetTask, CreateTask}
import repository.user.GetUserByUUID


class LogTask[F[+_]: Sync](
                            getProjectId: GetProjectByName[F],
                            getUserId: GetUserByUUID[F],
                            insertTask: CreateTask[F],
                            getTask: GetTask[F]) {

  def apply(work: LogTaskRequest, uuid: String): F[Either[LogTimeAppError, Task]] = {
    (for {
      projectId <- getExistingProjectId(work.projectName)
      userId <- getExistingUserId(uuid)
      taskId <- insertNewTask(work, projectId.id, userId)
      task <- getExistingTask(taskId)
    } yield task).value
  }

  private def getExistingProjectId(projectName: String): EitherT[F, LogTimeAppError, Project] =
    EitherT.fromOptionF(getProjectId(projectName), ProjectNotFound)

  private def getExistingUserId(userIdentification: String): EitherT[F, LogTimeAppError, Int] =
    EitherT.fromOptionF(getUserId(userIdentification), UserNotFound )

  private def insertNewTask(work: LogTaskRequest, projectId: Long, userId: Long): EitherT[F, LogTimeAppError, Int] =
    EitherT(insertTask(work, projectId, userId, ZonedDateTime.now(ZoneOffset.UTC).toLocalDateTime))

  private def getExistingTask(taskId: Long): EitherT[F, LogTimeAppError, Task] =
    EitherT.fromOptionF(getTask(taskId), TaskNotCreated )
}