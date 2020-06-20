package routes

import java.util.UUID

import akka.http.scaladsl.model.{HttpHeader, HttpResponse, StatusCodes}
import akka.http.scaladsl.server.directives.PathDirectives.path
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import akka.http.scaladsl.server.Directives._
import cats.effect.IO
import error.{AuthenticationNotSuccessful, LogTimeAppError, MapToErrorResponse}
import models.model.User
import io.circe.generic.auto._
import cats.implicits._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import service.auth.Auth
import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.server.Route
import models.Exists

object UserRoutes {

  private case class AuthorizationRequest(userUUID: UUID)

  private case class AuthResponse(token: String)

  def createUser(user: => IO[Either[LogTimeAppError, User]]) =
    pathPrefix("user" / "register") {
      post {
        complete(
          user
            .map(_.leftMap(MapToErrorResponse.user))
            .unsafeToFuture
        )
      }
    }


  def authorizeUser(userId: String => IO[Either[LogTimeAppError, Exists]])
                   (implicit auth: Auth): Route =
    pathPrefix("user" / "login") {
      post {
        entity(as[AuthorizationRequest]) { req => {
          complete(
            userId(req.userUUID.toString).map[ToResponseMarshallable] {
              case Left(value) => MapToErrorResponse.auth(value)
              case Right(value) => value match {
                case Exists(value) if value => AuthResponse(auth.token(req.userUUID.toString))
                case Exists(value) if !value => MapToErrorResponse.auth(AuthenticationNotSuccessful)
              }
            }.unsafeToFuture
          )
        }
        }
      }
    }
}