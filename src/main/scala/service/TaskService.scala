package service

import data.{ChangeProjectName, CreateProject, DeleteProject, Entities, LogTask, Queries}
import dbConnection.PostgresDb
import java.util.UUID

import akka.http.scaladsl.model.DateTime
import doobie.postgres._
import doobie.implicits._
import cats.effect.IO
import data.Entities._
import doobie.util.ExecutionContexts
import doobie.util.log.LogHandler
import error._

class TaskService() {

  val con = PostgresDb.xa
  implicit val cs = IO.contextShift(ExecutionContexts.synchronous)


  def logTask(task: LogTask): IO[Either[AppError, Task]] = {

    val z = for {
      projectId <- Queries.Project.getProjectId(task.projectName).unique
      userId <- Queries.User.getUserId(task.userIdentification).unique
      id <- Queries.Task.insert(task, projectId, userId).unique
      task <- Queries.Task.selectLastInsertedTask(id).unique
    } yield task

    z.transact(con).attemptSomeSqlState{
      case sqlstate.class23.EXCLUSION_VIOLATION => CannotLogNewTaskWithTheOverlappingTimeRangeForTheSameUser
      case sqlstate.class23.UNIQUE_VIOLATION => CannotLogNewTaskWithDuplicateTaskDescriptionUnderTheSameProject
    }
  }
}
