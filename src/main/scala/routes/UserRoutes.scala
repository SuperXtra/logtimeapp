package routes

import akka.http.scaladsl.model.{HttpHeader, HttpResponse, StatusCodes}
import akka.http.scaladsl.server.directives.PathDirectives.path
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import akka.http.scaladsl.server.Directives._
import cats.effect.IO
import error.AppError
import models.model.UserTb
import io.circe.generic.auto._
import cats.implicits._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import models.request.AuthorizationRequest
import service.auth.AuthService
import StatusCodes._
import akka.http.scaladsl.model.headers.Authorization

object UserRoutes {

  val authorization = new AuthService()

  def createUser(user: => IO[Either[AppError, UserTb]]) =
    path("user") {
      post {
        complete(
          user.map {
            case Right(newUser) => newUser.asRight
            case Left(value) => value.asLeft
          }.unsafeToFuture
        )
      }
    }


  def authorizeUser(userId: String => IO[Boolean]) =
    path("login") {
      post {
        entity(as[AuthorizationRequest]) { req => {
          complete(
            userId(req.userUUID).map {
              case true => HttpResponse(OK).withEntity(authorization.generateToken(req.userUUID))
              case false => HttpResponse(Unauthorized)
            }.unsafeToFuture
          )
        }
        }
      }
    }

}
