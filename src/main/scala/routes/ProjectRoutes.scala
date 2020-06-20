package routes

import akka.http.scaladsl.marshalling.ToResponseMarshallable
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
import models.ProjectId


object ProjectRoutes {

  val projectPath = "project"

  def createProject(req: (String, String) => IO[Either[LogTimeAppError, ProjectId]])
                   (implicit auth: Auth): Route =
    path(projectPath) {
      post {
        auth.apply { tokenClaims =>
          entity(as[CreateProjectRequest]) { project =>
            complete(
              req(project.projectName, tokenClaims("uuid").toString)
                .map(_.leftMap(MapToErrorResponse.project))
                .unsafeToFuture
            )
          }
        }
      }
    }


  def updateProject(req: (String, String, String) => IO[Either[LogTimeAppError, Unit]])
                   (implicit auth: Auth): Route =
    path(projectPath) {
      put {
        auth.apply { tokenClaims =>
          entity(as[ChangeProjectNameRequest]) { project =>
            complete(
              req(project.oldProjectName, project.projectName, tokenClaims("uuid").toString)
                .map(_.leftMap(MapToErrorResponse.project))
                .unsafeToFuture
            )
          }
        }
      }
    }

  def deleteProject(req: (String, String) => IO[Either[LogTimeAppError,Unit]])
                   (implicit auth: Auth): Route =
    path(projectPath) {
      delete {
        auth.apply { tokenClaims =>
          entity(as[DeleteProjectRequest]) { project =>
            complete(
              req(project.projectName, tokenClaims("uuid").toString)
                .map(_.leftMap(MapToErrorResponse.project))
                .unsafeToFuture
            )
          }
        }
      }
    }
}
