import LogTimeApp.system
import akka.actor.ActorSystem
import akka.event.{Logging, MarkerLoggingAdapter}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import cats.effect.{ContextShift, IO}
import com.softwaremill.macwire.wire
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

  implicit lazy val logger: MarkerLoggingAdapter = Logging.withMarker(system, "log-time-app")

  implicit val cs: ContextShift[IO] = IO.contextShift(ExecutionContexts.synchronous)
  val transactor = DatabaseContext.transactor(databaseConfig)

  lazy val deleteProjectWithTasks = wire[DeleteProjectWithTasks[IO]]
  lazy val insertProject = wire[InsertProject[IO]]
  lazy val updateProjectName = wire[UpdateProjectName[IO]]
  lazy val getProjectTasks = wire[GetProjectTasks[IO]]
  lazy val checkIfIsProjectOwner = wire[IsProjectOwner[IO]]

  lazy val getUserByUUID = wire[GetUserByUUID[IO]]
  lazy val createNewUser = wire[InsertUser[IO]]
  lazy val getUserById = wire[GetUserById[IO]]
  lazy val userExists = wire[UserExists[IO]]

  lazy val getUserTask = wire[GetUserTask[IO]]
  lazy val deleteTask = wire[DeleteTask[IO]]
  lazy val taskInsertUpdate = wire[ChangeTask[IO]]
  lazy val insertTask = wire[CreateTask[IO]]
  lazy val getTask = wire[GetTask[IO]]

  lazy val getReport = wire[GetReport[IO]]
  lazy val getDetailedReport = wire[GetDetailedReport[IO]]
  lazy val getProjectByName = wire[GetProjectByName[IO]]


  lazy val createNewProject = wire[CreateProject[IO]]
  lazy val deactivateProject = wire[DeactivateProject[IO]]
  lazy val updateProject = wire[UpdateProject[IO]]

  lazy val updateTask = wire[UpdateTask[IO]]
  lazy val logTask = wire[LogTask[IO]]
  lazy val deleteTaskService = wire[DeactivateTask[IO]]

  lazy val authenticateUser = wire[AuthenticateUser[IO]]
  lazy val insertNewUser = wire[CreateUser[IO]]

  lazy val projectTaskDurationReport = wire[GetProjectReport[IO]]
  lazy val detailReport = wire[GetStatisticsReport[IO]]
  lazy val projectWithTaskFilter = wire[GetParametrizedReport[IO]]

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