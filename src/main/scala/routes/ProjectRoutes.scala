package routes

import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives.{as, complete, delete, entity, path, post, put}
import akka.http.scaladsl.server.Route
import cats.effect.IO
import models.request.{ChangeProjectNameRequest, CreateProjectRequest, DeleteProjectRequest}
import models.model.Project
import io.circe.generic.auto._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import cats.implicits._
import service.auth.Authenticate
import error._


object ProjectRoutes {

  //TODO prepare responses

  val Authorization = new Authenticate()

  val projectPath = "project"

  def createProject(req: (CreateProjectRequest, String) => IO[Either[AppError, Int]]): Route =
    path(projectPath) {
      post {
        Authorization.authenticated { tokenClaims =>
          entity(as[CreateProjectRequest]) { project =>
            complete(
              req(project, tokenClaims("uuid").toString).map {
                case Right(x) => StatusCodes.OK ->  x.asRight
                case Left(x: AuthenticationNotSuccessful) => StatusCodes.Unauthorized -> x.asLeft
                case Left(x : AppError) => StatusCodes.ExpectationFailed -> x.asLeft
          }.unsafeToFuture
            )
          }
        }
      }
    }


  def updateProject(req: (ChangeProjectNameRequest, String) => IO[Either[AppError, Project]]): Route =
    path(projectPath) {
      put {
        Authorization.authenticated { tokenClaims =>
          entity(as[ChangeProjectNameRequest]) { project =>
            complete(
              req(project, tokenClaims("uuid").toString).map {
                case Right(updatedProject: Project) =>StatusCodes.OK -> updatedProject.asRight
                case Left(error: AppError) =>StatusCodes.ExpectationFailed -> error.asLeft
              }.unsafeToFuture
            )

          }
        }
      }
    }

  def deleteProject(req: (DeleteProjectRequest, String) => IO[Either[AppError, Unit]]): Route =
    path(projectPath) {
      delete {
        Authorization.authenticated { tokenClaims =>
          entity(as[DeleteProjectRequest]) { project =>
            complete(
              req(project, tokenClaims("uuid").toString).map {
                case Right(_) => StatusCodes.OK -> "Deleted Successfully".asRight
                case Left(value) =>StatusCodes.ExpectationFailed -> value.asLeft
              }.unsafeToFuture
            )
          }
        }
      }
    }
}
