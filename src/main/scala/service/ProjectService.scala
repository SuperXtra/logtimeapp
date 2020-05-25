package service

import cats.effect.{ContextShift, IO}
import java.time._

import cats.data.EitherT
import data.Entities._
import doobie.postgres._
import doobie.implicits._
import doobie.util.ExecutionContexts
import dbConnection.PostgresDb
import data._
import doobie.util.transactor.Transactor.Aux
import error._
import util.TimeZoneUTC

class ProjectService(con: Aux[IO, Unit])(implicit val contextShift: ContextShift[IO] ) {

  def createNewProject(project: CreateProject): IO[Either[AppError, Long]] = {
    (for {
      userId <- getExistingUserId(project.userIdentification)
      projectId <- insertProject(project.projectName, userId)
    } yield projectId).value
  }


  def updateProjectName(project: ChangeProjectName): IO[Either[AppError, Project]] = (for {
      userId <- getExistingUserId(project.userIdentification)
      _ <- changeProjectName(project.oldProjectName,project.projectName, userId)
      updatedRecord <- findProjectById(project.projectName)
    } yield updatedRecord).value

  def deleteProject(project: DeleteProject): EitherT[IO, AppError, Unit] = {
    //TODO try to move delete time to for-comp
    val deleteTime = TimeZoneUTC.currentTime
    for {
      userId <- getExistingUserId(project.userIdentification)
      _ <- deleteProject(userId, project.projectName, deleteTime)
      projectId <- findProjectById(project.projectName)
      _ <- deleteTasksForProject(projectId.id, deleteTime)
    } yield ()
  }

  def tasksAndDuration(projectName: String): IO[Either[AppError, ProjectReport]] = {
    (for {
      project <- findProjectById(projectName)
      projectTasks <- fetchTasksForProject(project.id)
    } yield {
      val totalDuration = projectTasks.map(_.duration).sum
      ProjectReport(project, Tasks(projectTasks), totalDuration)
    }).value
  }

  private def getExistingUserId(userIdentification: String): EitherT[IO, AppError, Long] = {
    EitherT.fromOptionF(Queries.User.getUserId(userIdentification).transact(con), UserNotFound)
  }

  private def insertProject(projectName: String, userId: Long): EitherT[IO, AppError, Long] = {
    EitherT(Queries.Project.insert(projectName, userId).transact(con).attemptSomeSqlState{
      case sqlstate.class23.EXCLUSION_VIOLATION => CannotLogNewTaskWithTheOverlappingTimeRangeForTheSameUser
      case sqlstate.class23.UNIQUE_VIOLATION => CannotLogNewTaskWithDuplicateTaskDescriptionUnderTheSameProject
    }
    )
  }

  private def changeProjectName(oldProjectName: String, projectName: String, userId: Long): EitherT[IO, AppError, Int] = {
    EitherT(Queries.Project.changeName(oldProjectName,projectName, userId).run.transact(con).attemptSomeSqlState{
      case sqlstate.class23.UNIQUE_VIOLATION => CannotChangeNameGivenProjectNameExistsAlready
    })
  }

  private def findProjectById(projectName: String): EitherT[IO, AppError, Project] = {
    EitherT.fromOptionF(Queries.Project.getProject(projectName).transact(con), ProjectNotCreated)
  }

  private def deleteProject(userId: Long, projectName: String, timeZoneUTC: ZonedDateTime): EitherT[IO, AppError, Int] = {
    println(s"should delete: ${userId}, ${projectName}, ${timeZoneUTC}")
    EitherT(Queries.Project.deleteProject(userId, projectName, timeZoneUTC).run.transact(con).attemptSomeSqlState{
      case x => {
        println(s"delete project error: $x")
        DeleteProjectUnsuccessful
      }

    })
  }

  private def deleteTasksForProject(id: Long, deleteTime: ZonedDateTime): EitherT[IO, AppError, Int] = {
    EitherT(Queries.Task.deleteTasksForProject(id, deleteTime).run.transact(con).attemptSomeSqlState{
      case x => {
        println(s"delete project task error: $x")
        DeleteProjectUnsuccessful
      }
    })
  }

  private def fetchTasksForProject(id: Long): EitherT[IO, AppError, List[Task]] = {
    EitherT(Queries.Task.fetchTasksForProject(id).transact(con).attemptSomeSqlState {
      case _ => FetchingTaskForProjectUnsuccessful
    })
  }
}
