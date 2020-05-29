package routes

import akka.http.scaladsl.server.Directives.{as, complete, delete, entity, path, post, put}
import akka.http.scaladsl.server.Route
import cats.effect.IO
import models.request.{DeleteTaskRequest, LogTaskRequest, UpdateTaskRequest}
import error._
import models.model.TaskTb
import io.circe.generic.auto._
import cats.implicits._
import routes.ProjectRoutes.Authorization
import service.auth.AuthService
//import cats.effect._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._

object TaskRoutes {

  val authorization = new AuthService()


  def logTask(logWorkDone: (LogTaskRequest, String) => IO[Either[AppError, TaskTb]]): Route =
    path("task") {
      post {
        Authorization.authenticated { tokenClaims =>
          entity(as[LogTaskRequest]) { task =>
            complete(
              logWorkDone(task, tokenClaims("uuid").toString).map {
                case Right(created) => created.asRight
                case Left(err) => err.asLeft
              }.unsafeToFuture
            )
          }
        }
      }
    }

  def updateTask(updateTask: (UpdateTaskRequest, String) => IO[Either[AppError, Long]]): Route =
    path("task") {
      put {
        Authorization.authenticated { tokenClaims =>
          entity(as[UpdateTaskRequest]) { update =>
            complete(
              updateTask(update, tokenClaims("uuid").toString).map {
                case Left(value) => value.asRight
                case Right(err) => err.asLeft
              }.unsafeToFuture()
            )
          }
        }
      }
    }


  def deleteTask(deleteTask: (DeleteTaskRequest,String) => IO[Either[AppError, Int]]): Route =
    path("task") {
      delete {
        Authorization.authenticated { tokenClaims =>
          entity(as[DeleteTaskRequest]) { delete =>
            complete(deleteTask(delete, tokenClaims("uuid").toString).map {
              case Right(value) => value.asRight
              case Left(ex) => ex.asLeft
            }.unsafeToFuture
            )
          }
        }
      }
    }
}
