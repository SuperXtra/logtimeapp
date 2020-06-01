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
import service.auth.Auth
import error._


object ProjectRoutes {

  //TODO prepare responses

  val projectPath = "project"

  def createProject(req: (CreateProjectRequest, String) => IO[Either[AppError, Int]])
                   (implicit auth: Auth): Route =
    path(projectPath) {
      post {
        auth.apply { tokenClaims =>
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


  def updateProject(req: (ChangeProjectNameRequest, String) => IO[Either[AppError, Project]])
                   (implicit auth: Auth): Route =
    path(projectPath) {
      put {
        auth.apply { tokenClaims =>
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

  def deleteProject(req: (DeleteProjectRequest, String) => IO[Either[AppError, Unit]])
                   (implicit auth: Auth): Route =
    path(projectPath) {
      delete {
        auth.apply { tokenClaims =>
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
