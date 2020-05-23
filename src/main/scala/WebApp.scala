import dbConnection.PostgresDb
import akka.http.scaladsl.model.{ContentTypes, DateTime, HttpEntity}
import java.util.UUID

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.{ActorMaterializer, Materializer}
import service.{ProjectService, UserService}
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import data.{ChangeProjectName, CreateProject, Entities}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.Directives
import util.JsonSupport
import spray.json._

import scala.io.StdIn

object WebApp extends App with JsonSupport {


  val service = new UserService()
  val projectService = new ProjectService()



  println("running")
//  val result = PostgresDb.test("54106ee8-a3ac-4dd6-834e-db8652e99068")
  def test() =service.createNewUser().map {
    case Left(value: Throwable) => complete(value)
    case Right(value: Entities.User) => complete(value)
  }.unsafeRunSync()



  implicit val system = ActorSystem("projectAppSystem")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  val route1 =
    path("createUser") {
      post {
        test()
      }
    }

  val route2 =
    path("createProject") {
      post {
        entity(as[CreateProject]) { project =>
          projectService.createNewProject(project).map{
            case Left(error) => complete(error)
            case Right(project) =>  complete(project)
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
              case (_, updateResult) if (updateResult == 0) => {
                complete("could not update, project name you are trying to update does not exist")
              }
            }
          }.unsafeRunSync()
        }
      }
    }

  val routes: Route = concat(route1,route2, route3)




  val bindingFuture = Http().bindAndHandle(routes, "localhost", 8080)
}
