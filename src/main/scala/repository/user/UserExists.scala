package repository.user

import cats.effect.Sync
import doobie.util.transactor.Transactor
import doobie.implicits._
import repository.query.UserQueries

class UserExists[F[_] : Sync](tx: Transactor[F]) {
  def apply(uuid: String): F[Boolean] = {
    UserQueries
      .userExists(uuid)
      .unique
      .transact(tx)
  }

}