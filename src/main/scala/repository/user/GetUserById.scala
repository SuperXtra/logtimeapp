package repository.user

import cats.effect.Sync
import error.{LogTimeAppError, UserNotFound}
import models.UserId
import models.model.User
import repository.query.UserQueries
import cats.implicits._
import slick.dbio.Effect
import scala.concurrent._
import ExecutionContext.Implicits.global
import scala.util.{Failure, Success}
import slick.jdbc.PostgresProfile.api._

class GetUserById[F[_]: Sync] {
  def apply(id: UserId): DBIOAction[Either[LogTimeAppError, User], NoStream, Effect.Read with Effect] = {
    UserQueries
      .getUserById(id)
      .asTry
      .flatMap {
        case Failure(_) => DBIO.successful(UserNotFound.asLeft)
        case Success(value) => value match {
          case Some(value) => DBIO.successful(value.asRight)
          case None =>DBIO.successful(UserNotFound.asLeft)
        }
      }
  }
}
