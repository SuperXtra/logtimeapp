import dbConnection.PostgresDb
import akka.http.scaladsl.model.{ContentTypes, DateTime, HttpEntity}
import java.util.UUID

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.{ActorMaterializer, Materializer}
import service.{ProjectService, TaskService, UserService}
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import data.{ChangeProjectName, CreateProject, DeleteProject, Entities, LogTask}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.Directives
import error.{DeleteUnsuccessfulProjectDoesNotExist, UpdateUnsuccessfulProjectDoesNotExist}
import util.JsonSupport
import spray.json._

import scala.io.StdIn

object WebApp extends App with JsonSupport {

  //TODO create global String to timestamp converter


  val service = new UserService()
  val projectService = new ProjectService()
  val taskService = new TaskService()


  implicit val system = ActorSystem("projectAppSystem")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  val route1 =
    path("createUser") {
      post {
        service.createNewUser().map {
          case Left(value: Throwable) => complete(value)
          case Right(value: Entities.User) => complete(value)
        }.unsafeRunSync()
      }
    }

  val route2 =
    path("createProject") {
      post {
        entity(as[CreateProject]) { project =>
          projectService.createNewProject(project).map {
            case Left(error) => complete(error)
            case Right(project) => complete(s"Created project with id: $project")
          }.unsafeRunSync()
        }
      }
    }

  val route3 =
    path("updateProject") {
      put {
        entity(as[ChangeProjectName]) { project =>
          projectService.updateProjectName(project).map {
            case Left(error) => complete(error)
            case Right(updated) => updated match {
              case (project, updateResult) if (updateResult == 1) => {
                complete(project)
              }
              case (_, updateResult) if (updateResult == 0) => complete(UpdateUnsuccessfulProjectDoesNotExist.toString)
            }
          }.unsafeRunSync()
        }
      }
    }

  val route4 =
    path("deleteProject") {
      delete {
        entity(as[DeleteProject]) { project =>
          projectService.deleteProject(project).map {
            case Left(error) => complete(error)
            case Right(deleted) => deleted match {
              case (project, updateResult) if (updateResult == 1) => {
                complete(project)
              }
              case (_, updateResult) if (updateResult == 0) => complete(DeleteUnsuccessfulProjectDoesNotExist.toString)
            }
          }.unsafeRunSync()
        }
      }
    }

  val route5 =
    path("createTask") {
      post {
        entity(as[LogTask]) { task =>
          taskService.logTask(task).map {
            case Left(error) =>  complete(error.toString)
            case Right(created) => complete(created)
          }.unsafeRunSync()
        }
      }
    }

  val routes: Route = concat(route1, route2, route3, route4, route5)


  val bindingFuture = Http().bindAndHandle(routes, "localhost", 8080)
}
