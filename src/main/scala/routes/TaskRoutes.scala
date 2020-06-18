package routes

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import cats.effect.IO
import models.request.{DeleteTaskRequest, LogTaskRequest, UpdateTaskRequest}
import error._
import models.model.Task
import io.circe.generic.auto._
import cats.implicits._
import service.auth.Auth
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import models.DeleteCount

object TaskRoutes {

  def logTask(logWorkDone: (LogTaskRequest, String) => IO[Either[LogTimeAppError, Task]])
             (implicit auth: Auth): Route =
    path("task") {
      post {
        auth.apply { tokenClaims =>
          entity(as[LogTaskRequest]) { task =>
            complete(
              logWorkDone(task, tokenClaims("uuid").toString)
                .map(_.leftMap(MapToErrorResponse.task))
                .unsafeToFuture
            )
          }
        }
      }
    }

  def updateTask(updateTask: (UpdateTaskRequest, String) => IO[Either[LogTimeAppError, Unit]])
                (implicit auth: Auth): Route =
    path("task") {
      put {
        auth.apply { tokenClaims =>
          entity(as[UpdateTaskRequest]) { update =>
            complete(
              updateTask(update, tokenClaims("uuid").toString)
                .map(_.leftMap(MapToErrorResponse.task))
                .unsafeToFuture
            )
          }
        }
      }
    }


  def deleteTask(deleteTask: (String, String, String) => IO[Either[LogTimeAppError, DeleteCount]])
                (implicit auth: Auth): Route =
    path("task") {
      delete {
        auth.apply { tokenClaims =>
          entity(as[DeleteTaskRequest]) { delete =>
            complete(deleteTask(delete.taskDescription, delete.projectName, tokenClaims("uuid").toString)
              .map(_.leftMap(MapToErrorResponse.task))
              .unsafeToFuture
            )
          }
        }
      }
    }
}
