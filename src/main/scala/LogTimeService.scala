import akka.actor.ActorSystem
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import cats.effect.{ContextShift, IO}
import com.typesafe.config.Config
import db.DatabaseContext
import doobie.util.ExecutionContexts
import pureconfig.ConfigSource
import repository.project._
import repository.task._
import repository.user._
import routes._
import service.project._
import service.report._
import service.task._
import service.user._
import pureconfig._
import pureconfig.generic.auto._
import repository.report.{GetDetailedReport, GetReport}
import service.auth.Auth
import com.typesafe.config.ConfigFactory
import config.{AuthConfig, DatabaseConfig}
import doobie.util.transactor.Transactor

import scala.concurrent.ExecutionContextExecutor

trait LogTimeService {
  val databaseConfiguration: Config = ConfigFactory.load("database-configuration.conf")
  val databaseConfig: DatabaseConfig = ConfigSource.fromConfig(databaseConfiguration).loadOrThrow[DatabaseConfig]
  val authConfiguration: Config = ConfigFactory.load("auth-configuration.conf")
  val authConfig: AuthConfig = ConfigSource.fromConfig(authConfiguration).loadOrThrow[AuthConfig]

  implicit val cs: ContextShift[IO] = IO.contextShift(ExecutionContexts.synchronous)
  val transactor = DatabaseContext.transactor(databaseConfig)

  val deleteProjectWithTasks = new DeleteProjectWithTasks[IO](transactor)
  val insertProject = new InsertProject[IO](transactor)
  val updateProjectName = new UpdateProjectName[IO](transactor)
  val getProjectTasks = new GetProjectTasks[IO](transactor)
  val checkIfIsProjectOwner = new IsProjectOwner[IO](transactor)

  val getUserByUUID = new GetUserByUUID[IO](transactor)
  val createNewUser = new InsertUser[IO](transactor)
  val getUserById = new GetUserById[IO](transactor)
  val userExists = new UserExists[IO](transactor)

  val getUserTask = new GetUserTask[IO](transactor)
  val deleteTask = new DeleteTask[IO](transactor)
  val taskInsertUpdate = new ChangeTask[IO](transactor)
  val insertTask = new CreateTask[IO](transactor)
  val getTask = new GetTask[IO](transactor)

  val getReport = new GetReport[IO](transactor)
  val getDetailedReport = new GetDetailedReport[IO](transactor)
  val getProjectByName = new GetProjectByName[IO](transactor)


  val createNewProject = new CreateProject[IO](getUserByUUID, insertProject)
  val deactivateProject = new DeactivateProject[IO](getUserByUUID,deleteProjectWithTasks, getProjectByName, checkIfIsProjectOwner)
  val updateProject = new UpdateProject[IO](getUserByUUID, updateProjectName)

  val updateTask = new UpdateTask[IO](getUserByUUID, getUserTask,taskInsertUpdate)
  val logTask = new LogTask[IO](getProjectByName, getUserByUUID, insertTask, getTask)
  val deleteTaskService = new DeactivateTask[IO](getProjectByName, getUserByUUID, deleteTask)

  val authenticateUser = new AuthenticateUser[IO](userExists)
  val insertNewUser = new CreateUser[IO](getUserById, createNewUser)

  val projectTaskDurationReport = new GetProjectReport[IO](getProjectByName, getProjectTasks)
  val detailReport = new GetStatisticsReport[IO](getDetailedReport)
  val projectWithTaskFilter = new GetParametrizedReport[IO](getReport)

  implicit val authentication: Auth = Auth(authConfig)

  val routes: Route = concat(
    TaskRoutes.logTask(logTask.apply),
    TaskRoutes.deleteTask(deleteTaskService.apply),
    TaskRoutes.updateTask(updateTask.apply),
    UserRoutes.createUser(insertNewUser.apply),
    UserRoutes.authorizeUser(authenticateUser.apply),
    ProjectRoutes.createProject(createNewProject.apply),
    ProjectRoutes.updateProject(updateProject.apply),
    ProjectRoutes.deleteProject(deactivateProject.apply),
    ReportRoutes.projectTasksReport(projectTaskDurationReport.apply),
    ReportRoutes.mainReport(projectWithTaskFilter.apply),
    ReportRoutes.detailedReport(detailReport.apply)
  )
}