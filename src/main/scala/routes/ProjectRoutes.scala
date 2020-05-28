package routes

import akka.http.scaladsl.server.Directives.{as, complete, delete, entity, path, post, put}
import akka.http.scaladsl.server.Route
import cats.effect.IO
import models.request.{ChangeProjectNameRequest, CreateProjectRequest, DeleteProjectRequest}
import error.AppError
import models.model.ProjectTb
import io.circe.generic.auto._
import cats.implicits._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._

object ProjectRoutes {

  //TODO prepare responses

  val projectPath = "project"

  def createProject(req: CreateProjectRequest => IO[Either[AppError, Long]]): Route =
    path(projectPath) {
      post {
        entity(as[CreateProjectRequest]) { project =>
          complete(
            req(project).map {
              case Right(project) => project.asRight
              case Left(error: AppError) => error.asLeft
            }.unsafeToFuture
          )
        }
      }
    }

  def updateProject(req: ChangeProjectNameRequest => IO[Either[AppError, ProjectTb]]): Route =
    path(projectPath) {
      put {
        entity(as[ChangeProjectNameRequest]) { project =>
          complete(
            req(project).map {
              case Right(updatedProject: ProjectTb) => updatedProject.asLeft
              case Left(error: AppError) => error.asRight
            }.unsafeToFuture
          )

        }
      }
    }

  def deleteProject(req: DeleteProjectRequest => IO[Either[AppError, Unit]]): Route =
    path(projectPath) {
      delete {
        entity(as[DeleteProjectRequest]) { project =>
          complete(
            req(project).map {
              case Right(_) => "Deleted Successfully".asRight
              case Left(value) => value.asLeft
            }.unsafeToFuture
          )
        }
      }
    }
}
