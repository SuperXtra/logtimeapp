package routes

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import cats.effect.IO
import models.request.{DeleteTaskRequest, LogTaskRequest, UpdateTaskRequest}
import errorMessages._
import models.model.Task
import io.circe.generic.auto._
import cats.implicits._
import service.auth.Auth
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._

object TaskRoutes {

  def logTask(logWorkDone: (LogTaskRequest, String) => IO[Either[AppBusinessError, Task]])
             (implicit auth: Auth): Route =
    path("task") {
      post {
        auth.apply { tokenClaims =>
          entity(as[LogTaskRequest]) { task =>
            complete(
              logWorkDone(task, tokenClaims("uuid").toString)
                .map(_.leftMap(LeftResponse.task))
                .unsafeToFuture
            )
          }
        }
      }
    }

  def updateTask(updateTask: (UpdateTaskRequest, String) => IO[Either[AppBusinessError, Unit]])
                (implicit auth: Auth): Route =
    path("task") {
      put {
        auth.apply { tokenClaims =>
          entity(as[UpdateTaskRequest]) { update =>
            complete(
              updateTask(update, tokenClaims("uuid").toString)
                .map(_.leftMap(LeftResponse.task))
                .unsafeToFuture
            )
          }
        }
      }
    }


  def deleteTask(deleteTask: (DeleteTaskRequest,String) => IO[Either[AppBusinessError, Int]])
                (implicit auth: Auth): Route =
    path("task") {
      delete {
        auth.apply { tokenClaims =>
          entity(as[DeleteTaskRequest]) { delete =>
            complete(deleteTask(delete, tokenClaims("uuid").toString)
              .map(_.leftMap(LeftResponse.task))
              .unsafeToFuture
            )
          }
        }
      }
    }
}
