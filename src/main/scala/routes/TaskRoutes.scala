package routes

import akka.http.scaladsl.server.Directives.{as, complete, delete, entity, path, post, put}
import akka.http.scaladsl.server.Route
import cats.effect.IO
import models.request.{DeleteTaskRequest, LogTaskRequest, UpdateTaskRequest}
import error._
import models.model.TaskTb
import io.circe.generic.auto._
import cats.implicits._
//import cats.effect._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._

object TaskRoutes {

  def logTask(logWorkDone: LogTaskRequest => IO[Either[AppError, TaskTb]]): Route =
    path("task") {
      post {
        entity(as[LogTaskRequest]) { task =>
          complete(
            logWorkDone(task).map {
              case Right(created) => created.asRight
              case Left(err) => err.asLeft
            }.unsafeToFuture
          )
        }
      }
    }

  def updateTask(updateTask: UpdateTaskRequest => IO[Either[AppError, Long]]): Route =
    path("task") {
      put {
        entity(as[UpdateTaskRequest]) { update =>
          complete(
            updateTask(update).map {
              case Left(value) => value.asRight
              case Right(err) => err.asLeft
            }.unsafeToFuture()
          )
        }
      }
    }


  def deleteTask(deleteTask: DeleteTaskRequest => IO[Either[AppError, Int]]): Route =
    path("task") {
      delete {
        entity(as[DeleteTaskRequest]) { delete =>
          complete(deleteTask(delete).map {
            case Right(value) => value.asRight
            case Left(ex) => ex.asLeft
          }.unsafeToFuture
          )
        }
      }
    }
}
