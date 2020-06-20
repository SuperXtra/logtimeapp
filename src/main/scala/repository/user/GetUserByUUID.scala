package repository.user

import cats.effect.Sync
import cats.implicits._
import doobie.implicits._
import doobie.util.transactor.Transactor
import error.{LogTimeAppError, ProjectNotCreated}
import models.UserId
import models.model.User
import repository.query.UserQueries
import slick.jdbc.PostgresProfile.api._

import scala.concurrent._
import ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

class GetUserByUUID[F[_] : Sync]() {
  def apply(userIdentification: String): DBIOAction[Either[LogTimeAppError, User], NoStream, Effect.All with Effect] =
    UserQueries
      .getUserIdByUUIDSlick(userIdentification)
      .asTry
      .flatMap {
        case Failure(_) => DBIO.successful(ProjectNotCreated.asLeft)
        case Success(user) => user match {
          case Some(value) => DBIO.successful(value.asRight)
          case None => DBIO.successful(ProjectNotCreated.asLeft)
        }
      }
}