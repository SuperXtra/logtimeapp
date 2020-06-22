package repository.user

import cats.effect.Sync
import error.{LogTimeAppError, UserNotFound}
import models.Exists
import repository.query.UserQueries
import scala.concurrent._
import ExecutionContext.Implicits.global
import slick.jdbc.PostgresProfile.api._
import cats.implicits._

import scala.util.{Failure, Success}

class UserExists[F[_] : Sync] {
  def apply(uuid: String): DBIOAction[Either[LogTimeAppError, Exists], NoStream, Effect.All with Effect] = {
    UserQueries
      .userExists(uuid)
      .asTry
      .flatMap {
        case Failure(_) => DBIO.successful(UserNotFound.asLeft)
        case Success(value) => DBIO.successful(Exists(value).asRight)
      }
  }

}