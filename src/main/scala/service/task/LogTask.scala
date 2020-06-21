package service.task

import java.time.{ZoneOffset, ZonedDateTime}

import akka.event.MarkerLoggingAdapter
import cats.effect.{ContextShift, IO, Sync}
import db.DatabaseContext
import models.request.LogTaskRequest
import error.{LogTimeAppError, ProjectNotFound, TaskNotCreated, UserNotFound}
import models.{ProjectId, TaskId, UserId}
import models.model.{Project, Task}
import repository.project.GetProjectByName
import repository.task.{CreateTask, GetTask}
import repository.user.GetUserByUUID
import slick.jdbc.PostgresProfile
import slick.jdbc.PostgresProfile.api._
import utils.EitherT
import scala.concurrent._
import ExecutionContext.Implicits.global
import db.RunDBIOAction._

class LogTask[F[+_]: Sync](
                            getProjectId: GetProjectByName[F],
                            getUserId: GetUserByUUID[F],
                            insertTask: CreateTask[F],
                            getTask: GetTask[F])
                          (implicit db: Database,
                           logger: MarkerLoggingAdapter,
                           ec: ContextShift[IO]) {

  def apply(work: LogTaskRequest, uuid: String): IO[Either[LogTimeAppError, Task]] = (for {
    projectId <- getExistingProjectId(work.projectName)
    userId <- getExistingUserId(uuid)
    taskId <- insertNewTask(work, projectId.id, userId.userId)
    task <- getExistingTask(taskId)
  } yield task).value.transactionally.exec

  private def getExistingProjectId(projectName: String)=
    EitherT(getProjectId(projectName))

  private def getExistingUserId(userIdentification: String) =
    EitherT(getUserId(userIdentification))

  private def insertNewTask(work: LogTaskRequest, projectId: ProjectId, userId: UserId) =
    EitherT(insertTask(work, projectId, userId, ZonedDateTime.now(ZoneOffset.UTC).toLocalDateTime))

  private def getExistingTask(taskId: TaskId)=
    EitherT(getTask(taskId))
}