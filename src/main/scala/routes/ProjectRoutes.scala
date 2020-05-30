package routes

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives.{as, complete, delete, entity, path, post, put}
import akka.http.scaladsl.server.Route
import cats.effect.IO
import models.request.{ChangeProjectNameRequest, CreateProjectRequest, DeleteProjectRequest}
import error.AppError
import models.model.Project
import io.circe.generic.auto._
import cats.implicits._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import service.auth.Authenticate

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
                case Right(_) => StatusCodes.Created
                case Left(error) => StatusCodes.ExpectationFailed -> error.asLeft
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
                case Right(updatedProject: Project) => updatedProject.asLeft
                case Left(error: AppError) =>StatusCodes.ExpectationFailed -> error.asRight
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
                case Right(_) => "Deleted Successfully".asRight
                case Left(value) =>StatusCodes.ExpectationFailed -> value.asLeft
              }.unsafeToFuture
            )
          }
        }
      }
    }
}
