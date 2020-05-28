package routes

import akka.http.scaladsl.server.directives.PathDirectives.path
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import akka.http.scaladsl.server.Directives._
import cats.effect.IO
import error.AppError
import models.model.UserTb
import io.circe.generic.auto._
import cats.implicits._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._

object UserRoutes {

  def createUser(user: () => IO[Either[AppError, UserTb]]) =
  path("user") {
    post {
      complete(
        user().map {
          case Right(newUser) =>newUser.asRight
          case Left(value) => value.asLeft
        }.unsafeToFuture
      )
    }
  }
}
