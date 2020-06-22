package service.user

import akka.event.MarkerLoggingAdapter
import cats.effect.{ContextShift, IO, Sync}
import cats.implicits._
import error.LogTimeAppError
import models.Exists
import repository.user._
import slick.jdbc.PostgresProfile.api._
import db.RunDBIOAction._

class AuthenticateUser[F[+_] : Sync](exists: UserExists[F])
                                    (implicit db: Database,
                                     logger: MarkerLoggingAdapter,
                                     ec: ContextShift[IO]) {

  def apply(uuid: String): IO[Either[LogTimeAppError, Exists]] = {
   logging.checkingWhetherUserExists(uuid)
    exists(uuid).exec
  }
}
