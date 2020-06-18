package repository.user

import cats.effect.Sync
import cats.implicits._
import doobie.implicits._
import doobie.util.transactor.Transactor
import models.UserId
import repository.query.UserQueries

class GetUserByUUID[F[_] : Sync](tx: Transactor[F]) {
  def apply(userIdentification: String): F[Option[UserId]] = {
    UserQueries
      .getUserIdByUUID(userIdentification)
      .option
      .map {
        case Some(value) => UserId(value).some
        case None => None
      }
      .transact(tx)
  }
}
