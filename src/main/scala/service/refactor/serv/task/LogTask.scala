package service.refactor.serv.task

import cats.data.EitherT
import cats.effect.Sync
import data.Entities.{Project, Task}
import data.LogTaskModel
import error.{AppError, ProjectNotFound, TaskNotCreated, UserNotFound}
import service.refactor.repo.project._
import service.refactor.repo.task._
import service.refactor.repo.user._


class LogTask[F[+_]: Sync](
                            getProjectId: FindProjectById[F],
                            getUserId: GetExistingUserId[F],
                            insertTask: InsertTask[F],
                            getTask: GetTask[F]) {

  def apply(work: LogTaskModel): F[Either[AppError, Task]] = {
    (for {
      projectId <- getExistingProjectId(work.projectName)
      userId <- getExistingUserId(work.userIdentification)
      taskId <- insertNewTask(work, projectId.id, userId)
      task <- getExistingTask(taskId)
    } yield task).value
  }

  private def getExistingProjectId(projectName: String): EitherT[F, AppError, Project] =
    EitherT.fromOptionF(getProjectId(projectName), ProjectNotFound)

  private def getExistingUserId(userIdentification: String): EitherT[F, AppError, Long] =
    EitherT.fromOptionF(getUserId(userIdentification), UserNotFound)

  private def insertNewTask(work: LogTaskModel, projectId: Long, userId: Long): EitherT[F, AppError, Long] =
    EitherT(insertTask(work, projectId, userId))

  private def getExistingTask(taskId: Long): EitherT[F, AppError, Task] =
    EitherT.fromOptionF(getTask(taskId), TaskNotCreated)
}
