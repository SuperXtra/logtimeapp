package routes

import akka.http.scaladsl.server.directives.PathDirectives.path
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import akka.http.scaladsl.server.Directives._
import cats.effect.IO
import error.AppError
import models.model.UserTb
import util.JsonSupport

object UserRoutes extends JsonSupport{

  def createUser(user: () => IO[Either[AppError, UserTb]]) =
  path("user") {
    post {
        user().map {
          case Left(value) => complete(value.toString)
          case Right(newUser) =>complete(newUser)
        }.unsafeRunSync()
    }
  }
}
