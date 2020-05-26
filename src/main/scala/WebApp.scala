import java.time.ZonedDateTime

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import cats.effect.{ContextShift, IO}
import util.JsonSupport

import scala.concurrent.Future
import service._
import error._
import data._
import dbConnection.PostgresDb
import doobie.util.ExecutionContexts
import doobie.util.transactor.Transactor.Aux
import service.refactor.serv.project.CreateNewProject
import service.refactor.repo.project.{DeleteProjectR, FindProjectById, InsertProject, UpdateProjectName}
import service.refactor.repo.task.DeleteTasks
import service.refactor.repo.user.{CreateUser, GetExistingUserId, UserById}
import service.refactor.serv.user.CreateNewUser
import spray.json._

object WebApp extends App with JsonSupport {

  //TODO create global String to timestamp converter
  implicit val cs: ContextShift[IO] = IO.contextShift(ExecutionContexts.synchronous)
  val connection: Aux[IO, Unit] = PostgresDb.xa


  val service = new UserService(connection)
  val projectService = new ProjectService(connection)
  val taskService = new TaskService(connection)


  val deleteProjectR = new DeleteProjectR[IO](connection)
  val deleteTasks = new DeleteTasks[IO](connection)
  val findProjectById = new FindProjectById[IO](connection)
  val getExistingUserId = new GetExistingUserId[IO](connection)
  val insertProject = new InsertProject[IO](connection)
  val updateProjectName = new UpdateProjectName[IO](connection)
  val createNewUser = new CreateUser[IO](connection)
  val userById = new UserById[IO](connection)

  val createNewProjectService = new CreateNewProject[IO](getExistingUserId, insertProject)
  val createNewUserService = new CreateNewUser[IO](userById, createNewUser)





  implicit val system = ActorSystem("projectAppSystem")
  implicit val executionContext = system.dispatcher

  println(ZonedDateTime.now().toString)

  val route1 =
    path("user") {
      post {
        createNewUserService().map {
          case Left(value) => complete(value.toString)
          case Right(newUser) =>complete(newUser)
        }.unsafeRunSync()
      }
    }

  val route2 =
    path("project") {
      post {
        entity(as[CreateProject]) { project =>
          complete(
            createNewProjectService(project).map {
              case Right(project) => s"Created project with id: $project"
              case Left(error: AppError) => error.toString
            }.unsafeToFuture()
          )
        }
      }
    }

  val route3 =
    path("project") {
      put {
        entity(as[ChangeProjectName]) { project =>
          complete(
            projectService.updateProjectName(project).map {
              case Right(updatedProject: Entities.Project) => updatedProject.projectName
              case Left(error: AppError) => error.toString
            }.unsafeToFuture()
          )

        }
      }
    }

  val route4 =
    path("project") {
      delete {
        entity(as[DeleteProject]) { project =>
          projectService.deleteProject(project).value.map {
            case Right(_) =>complete("Deleted")
            case Left(value) => complete(value.toString)
          }.unsafeRunSync()
        }
      }
    }

  val route5 =
    path("task") {
      post {
        entity(as[LogTaskModel]) { task =>
          taskService.logTask(task).map {
            case Left(error) => complete(error.toString)
            case Right(created) => complete(created)
          }.unsafeRunSync()
        }
      }
    }

  val route6 =
    path("task") {
      delete {
        entity(as[DeleteTask]) { task =>
          taskService.deleteTask(task).map {
            case Left(value) => complete(value.toString)
            case Right(value) => value match {
              case x => complete(s"number of affected rows: $x")
            }
          }.unsafeRunSync()
        }
      }
    }

  val route7 =
    path("task") {
      put {
        entity(as[UpdateTask]) { update =>
          taskService.updateTask(update).map {
            case Left(value) => complete(value.toString)
            case Right(value) => value match {
              case x => complete(s"Updated succesfully, new id: ${x}")
            }
          }.unsafeRunSync()
        }
      }
    }

  val route8 =
    path("project" / Segment) { projectName: String =>
      get {
          projectService.tasksAndDuration(projectName).map {
          case Left(value: AppError) => complete {
            println("error")
            value.toString
          }
          case Right(report) => complete{
            println("right")

            println(report.toString)
            report
          }
        }.unsafeRunSync()
      }
    }

  val routes: Route = concat(route1, route2, route3, route4, route5, route6, route7, route8)


  val bindingFuture: Future[Http.ServerBinding] = Http().bindAndHandle(routes, "localhost", 8082)
}
