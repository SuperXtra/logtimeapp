package repository.user

import java.util.UUID

import cats.effect.Sync
import doobie.util.transactor.Transactor
import doobie.implicits._
import error.CannotCreateUserWithGeneratedUUID
import repository.query.UserQueries

class InsertUser[F[_]: Sync](tx: Transactor[F]) {
  def apply(uuid: String): F[Either[CannotCreateUserWithGeneratedUUID.type, Int]] = {
    UserQueries
      .insertUser(uuid)
      .unique
      .transact(tx)
      .attemptSomeSqlState {
        case _ => CannotCreateUserWithGeneratedUUID
      }
  }

}
