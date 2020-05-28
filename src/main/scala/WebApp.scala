import java.time.ZonedDateTime

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import cats.effect.{ContextShift, IO}
import com.typesafe.config.ConfigFactory
import config.DatabaseConfig
import util.JsonSupport

import scala.concurrent.Future
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
import repository.report.Report
import spray.json._

object WebApp extends App with JsonSupport {

  val config = ConfigFactory.load("database-configuration.conf")
  val databaseConfig = ConfigSource.fromConfig(config).loadOrThrow[DatabaseConfig]
  //TODO create global String to timestamp converter

  implicit val system = ActorSystem("projectAppSystem")
  implicit val executionContext = system.dispatcher


  implicit val cs: ContextShift[IO] = IO.contextShift(ExecutionContexts.synchronous)
  val tx = DatabaseContext.transactor(databaseConfig)


//  val taskService = new TaskService(connection)


  val deleteProjectR = new DeleteProjectR[IO](tx)
  val deleteTasks = new DeleteTasks[IO](tx)
  val findProjectById = new FindProjectById[IO](tx)
  val getExistingUserId = new GetExistingUserId[IO](tx)
  val insertProject = new InsertProject[IO](tx)
  val updateProjectName = new UpdateProjectName[IO](tx)
  val createNewUser = new CreateUser[IO](tx)
  val userById = new UserById[IO](tx)
  val deleteProject = new DeleteProjectR[IO](tx)
  val getProjectTasks = new GetProjectTasks[IO](tx)
  val getUserTask = new GetUserTask[IO](tx)
  val deleteTask = new TaskDelete[IO](tx)
  val taskInsertUpdate = new TaskInsertUpdate[IO](tx)
  val insertTask = new InsertTask[IO](tx)
  val getTask = new GetTask[IO](tx)
  val report = new Report[IO](tx)


  val createNewProjectService = new CreateNewProject[IO](getExistingUserId, insertProject)
  val deactivateProjectService = new DeactivateProject[IO](getExistingUserId,deleteProject, findProjectById, deleteTasks)
  val updateProjectService = new UpdateProject[IO](getExistingUserId, updateProjectName, findProjectById)
  val createNewUserService = new CreateNewUser[IO](userById, createNewUser)
  val updateTaskService = new UpdateTas[IO](getExistingUserId, getUserTask, deleteTask,taskInsertUpdate)
  val logTaskService = new LogTask[IO](findProjectById, getExistingUserId, insertTask, getTask)
  val projectTaskDurationReport = new ProjectTasksDurationReport[IO](findProjectById, getProjectTasks)
  val deleteTaskService = new DeleteTas[IO](findProjectById, getExistingUserId, deleteTask)
  val projectWithTaskFilter = new ProjectWithTasks[IO](report)

  val routes: Route = concat(
    TaskRoutes.logTask(logTaskService.apply),
    TaskRoutes.deleteTask(deleteTaskService.apply),
    TaskRoutes.updateTask(updateTaskService.apply),
    UserRoutes.createUser(createNewUserService.apply),
    ProjectRoutes.createProject(createNewProjectService.apply),
    ProjectRoutes.updateProject(updateProjectService.apply),
    ProjectRoutes.deleteProject(deactivateProjectService.apply),
    ReportRoutes.projectTasksReport(projectTaskDurationReport.apply),
    ReportRoutes.mainReport(projectWithTaskFilter.apply)
  )


  val bindingFuture: Future[Http.ServerBinding] = Http().bindAndHandle(routes, "localhost", 8080)
}
