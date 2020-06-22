package repository.user

import cats.effect.Sync
import doobie.util.transactor.Transactor
import error.{CannotCreateUserWithGeneratedUUID, LogTimeAppError}
import models.UserId
import repository.query.UserQueries
import cats.implicits._
import scala.concurrent._
import ExecutionContext.Implicits.global
import scala.util.{Failure, Success}
import slick.jdbc.PostgresProfile.api._

class InsertUser[F[_]: Sync] {
  def apply(uuid: String): DBIOAction[Either[LogTimeAppError, UserId], NoStream, Effect.Write with Effect] = {
    UserQueries
      .insertUser(uuid)
      .asTry
      .flatMap {
        case Failure(_) => DBIO.successful(CannotCreateUserWithGeneratedUUID.asLeft)
        case Success(value) =>DBIO.successful(value.asRight)
      }
  }
}