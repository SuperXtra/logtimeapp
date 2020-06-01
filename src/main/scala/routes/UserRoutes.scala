package routes

import akka.http.scaladsl.model.{HttpHeader, HttpResponse, StatusCodes}
import akka.http.scaladsl.server.directives.PathDirectives.path
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import akka.http.scaladsl.server.Directives._
import cats.effect.IO
import errorMessages.AppBusinessError
import models.model.User
import io.circe.generic.auto._
import cats.implicits._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import models.request.AuthorizationRequest
import service.auth.Auth
import StatusCodes._

object UserRoutes {

  def createUser(user: => IO[Either[AppBusinessError, User]]) =
    path("user") {
      post {
        complete(
          user
            .map(_.leftMap(LeftResponse(_)))
            .unsafeToFuture
        )
      }
    }


  def authorizeUser(userId: String => IO[Boolean])
                   (implicit auth: Auth) =
    path("login") {
      post {
        entity(as[AuthorizationRequest]) { req => {
          complete(
            userId(req.userUUID).map {
              case true => HttpResponse(OK).withEntity(auth.token(req.userUUID))
              case false => HttpResponse(Unauthorized)
            }.unsafeToFuture
          )
        }
        }
      }
    }

}
