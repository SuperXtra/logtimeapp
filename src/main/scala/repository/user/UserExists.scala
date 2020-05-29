package repository.user

import cats.effect.Sync
import doobie.util.transactor.Transactor
import repository.queries.User
import doobie.implicits._

class UserExists[F[_] : Sync](tx: Transactor[F]) {
  def apply(uuid: String): F[Boolean] = {
    User
      .userExists(uuid)
      .unique
      .transact(tx)
  }

}