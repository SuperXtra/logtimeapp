package routes

import akka.http.scaladsl.server.Directives.{as, complete, delete, entity, path, post, put}
import cats.effect.IO
import models.request.{DeleteTaskRequest, LogTaskRequest, UpdateTaskRequest}
import error._
import models.model.TaskTb
import util.JsonSupport

object TaskRoutes extends JsonSupport{

  def logTask(logWorkDone: LogTaskRequest => IO[Either[AppError, TaskTb]]) =
    path("task") {
      post {
        entity(as[LogTaskRequest]) { task =>
          complete(logWorkDone(task).map {
            case Right(created) => created
            case Left(ex) => throw AppException(ex.toString) // TODO fix it
          }.unsafeToFuture
          )
        }
      }
    }


  def updateTask(updateTask: UpdateTaskRequest => IO[Either[AppError, Long]]) =
    path("task") {
      put {
        entity(as[UpdateTaskRequest]) { update =>
          complete(
            updateTask(update).map {
              case Left(value) => value.toString
              case Right(value) => s"Updated succesfully, new id: ${value}"
            }.unsafeToFuture()
          )

        }
      }
    }

  def deleteTask(deleteTask: DeleteTaskRequest => IO[Either[AppError, Int]]) =
    path("task") {
      delete {
        entity(as[DeleteTaskRequest]) { delete =>
          complete(deleteTask(delete).map {
            case Right(value) => value.toString // TODO prepare nice response for delete
            case Left(ex) => throw AppException(ex.toString)
          }.unsafeToFuture
          )
        }
      }
    }

}
