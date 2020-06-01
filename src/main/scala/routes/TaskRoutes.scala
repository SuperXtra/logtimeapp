package routes

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives.{as, complete, delete, entity, path, post, put}
import akka.http.scaladsl.server.Route
import cats.effect.IO
import models.request.{DeleteTaskRequest, LogTaskRequest, UpdateTaskRequest}
import error._
import models.model.Task
import io.circe.generic.auto._
import cats.implicits._
import service.auth.Auth
//import cats.effect._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._

object TaskRoutes {

  def logTask(logWorkDone: (LogTaskRequest, String) => IO[Either[AppError, Task]])
             (implicit auth: Auth): Route =
    path("task") {
      post {
        auth.apply { tokenClaims =>
          entity(as[LogTaskRequest]) { task =>
            complete(
              logWorkDone(task, tokenClaims("uuid").toString).map {
                case Right(created) => StatusCodes.OK -> created.asRight
                case Left(err) => StatusCodes.ExpectationFailed -> err.asLeft
              }.unsafeToFuture
            )
          }
        }
      }
    }

  def updateTask(updateTask: (UpdateTaskRequest, String) => IO[Either[AppError, Long]])
                (implicit auth: Auth): Route =
    path("task") {
      put {
        auth.apply { tokenClaims =>
          entity(as[UpdateTaskRequest]) { update =>
            complete(
              updateTask(update, tokenClaims("uuid").toString).map {
                case Left(value) => StatusCodes.OK -> value.asRight
                case Right(err) => StatusCodes.ExpectationFailed -> err.asLeft
              }.unsafeToFuture()
            )
          }
        }
      }
    }


  def deleteTask(deleteTask: (DeleteTaskRequest,String) => IO[Either[AppError, Int]])
                (implicit auth: Auth): Route =
    path("task") {
      delete {
        auth.apply { tokenClaims =>
          entity(as[DeleteTaskRequest]) { delete =>
            complete(deleteTask(delete, tokenClaims("uuid").toString).map {
              case Right(value) => StatusCodes.OK -> value.asRight
              case Left(ex) => StatusCodes.ExpectationFailed -> ex.asLeft
            }.unsafeToFuture
            )
          }
        }
      }
    }
}
