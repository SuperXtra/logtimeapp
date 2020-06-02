import java.time.ZonedDateTime

import WebApp.authConfig
import akka.actor.{ActorLogging, ActorSystem}
import akka.event.{Logging, LoggingAdapter, MarkerLoggingAdapter}
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import cats.effect.{ContextShift, IO}
import com.typesafe.config.ConfigFactory
import config.{AuthConfig, DatabaseConfig}

import scala.concurrent.{ExecutionContextExecutor, Future}
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
import repository.report.{DetailedReport, Report}
import service.auth.Auth

object WebApp extends App {

  val databaseConfiguration = ConfigFactory.load("database-configuration.conf")
  val databaseConfig = ConfigSource.fromConfig(databaseConfiguration).loadOrThrow[DatabaseConfig]
  val authConfiguration = ConfigFactory.load("auth-configuration.conf")
  val authConfig: AuthConfig = ConfigSource.fromConfig(authConfiguration).loadOrThrow[AuthConfig]


  implicit val system: ActorSystem = ActorSystem("projectAppSystem")
  implicit val logger: MarkerLoggingAdapter = Logging.withMarker(system, "test")
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  implicit val cs: ContextShift[IO] = IO.contextShift(ExecutionContexts.synchronous)
  val tx = DatabaseContext.transactor(databaseConfig)

  val deleteProjectR = new DeleteProjectWithTasks[IO](tx)
  val getExistingUserId = new GetExistingUserId[IO](tx)
  val insertProject = new InsertProject[IO](tx)
  val updateProjectName = new UpdateProjectName[IO](tx)
  val createNewUser = new CreateUser[IO](tx)
  val userById = new UserById[IO](tx)
  val deleteProject = new DeleteProjectWithTasks[IO](tx)
  val getProjectTasks = new GetProjectTasks[IO](tx)
  val getUserTask = new GetUserTask[IO](tx)
  val deleteTask = new DeleteTask[IO](tx)
  val taskInsertUpdate = new TaskInsertUpdate[IO](tx)
  val insertTask = new InsertTask[IO](tx)
  val getTask = new GetTask[IO](tx)
  val report = new Report[IO](tx)
  val userExists = new UserExists[IO](tx)
  val detailedReport = new DetailedReport[IO](tx)
  val checkIfIsProjectOwner = new CheckIfIsProjectOwner[IO](tx)
  val findProjectByName = new FindProjectByName[IO](tx)

  val createNewProjectService = new ProjectCreate[IO](getExistingUserId, insertProject)
  val deactivateProjectService = new ProjectDeactivate[IO](getExistingUserId,deleteProject, findProjectByName, checkIfIsProjectOwner)
  val updateProjectService = new ProjectUpdate[IO](getExistingUserId, updateProjectName, findProjectByName)
  val createNewUserService = new UserCreate[IO](userById, createNewUser)
  val updateTaskService = new TaskUpdate[IO](getExistingUserId, getUserTask,taskInsertUpdate)
  val logTaskService = new TaskLog[IO](findProjectByName, getExistingUserId, insertTask, getTask)
  val projectTaskDurationReport = new OverviewReport[IO](findProjectByName, getProjectTasks)
  val deleteTaskService = new TaskDelete[IO](findProjectByName, getExistingUserId, deleteTask)
  val projectWithTaskFilter = new DetailReport[IO](report)
  val authenticateUser = new UserAuthenticate[IO](userExists)
  val detailReport = new AdditionalReport[IO](detailedReport)

  implicit val authentication: Auth = Auth(authConfig)

  val routes: Route = concat(
    TaskRoutes.logTask(logTaskService.apply),
    TaskRoutes.deleteTask(deleteTaskService.apply),
    TaskRoutes.updateTask(updateTaskService.apply),
    UserRoutes.createUser(createNewUserService.apply()),
    UserRoutes.authorizeUser(authenticateUser.apply),
    ProjectRoutes.createProject(createNewProjectService.apply),
    ProjectRoutes.updateProject(updateProjectService.apply),
    ProjectRoutes.deleteProject(deactivateProjectService.apply),
    ReportRoutes.projectTasksReport(projectTaskDurationReport.apply),
    ReportRoutes.mainReport(projectWithTaskFilter.apply),
    ReportRoutes.detailedReport(detailReport.apply)
  )


  val bindingFuture: Future[Http.ServerBinding] = Http().bindAndHandle(routes, "localhost", 8080)
}
