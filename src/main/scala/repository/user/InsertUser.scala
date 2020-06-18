package repository.user

import java.util.UUID

import cats.effect.Sync
import doobie.util.transactor.Transactor
import doobie.implicits._
import error.CannotCreateUserWithGeneratedUUID
import models.UserId
import repository.query.UserQueries

class InsertUser[F[_]: Sync](tx: Transactor[F]) {
  def apply(uuid: String): F[Either[CannotCreateUserWithGeneratedUUID.type, UserId]] = {
    UserQueries
      .insertUser(uuid)
      .unique
      .map(UserId)
      .transact(tx)
      .attemptSomeSqlState {
        case _ => CannotCreateUserWithGeneratedUUID
      }
  }

}
