package routes

import akka.http.scaladsl.server.Directives.{as, complete, delete, entity, path, post, put}
import akka.http.scaladsl.server.Route
import cats.data.EitherT
import cats.effect.IO
import models.request.{ChangeProjectNameRequest, CreateProjectRequest, DeleteProjectRequest}
import error.AppError
import models.model.ProjectTb
import util.JsonSupport

object ProjectRoutes extends JsonSupport{

  //TODO prepare responses

  val projectPath = "project"

  def createProject(req: CreateProjectRequest => IO[Either[AppError, Long]]): Route =
    path(projectPath) {
      post {
        entity(as[CreateProjectRequest]) { project =>
          complete(
            req(project).map {
              case Right(project) => s"Created project with id: $project"
              case Left(error: AppError) => error.toString
            }.unsafeToFuture()
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
              case Right(updatedProject: ProjectTb) => updatedProject.projectName
              case Left(error: AppError) => error.toString
            }.unsafeToFuture()
          )

        }
      }
    }

  def deleteProject(req: DeleteProjectRequest => IO[Either[AppError, Unit]]): Route =
    path(projectPath) {
      delete {
        entity(as[DeleteProjectRequest]) { project =>
          req(project).map {
            case Right(_) => complete("Deleted")
            case Left(value) => complete(value.toString)
          }.unsafeRunSync()
        }
      }
    }

}
