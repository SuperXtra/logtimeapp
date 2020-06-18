package repository.user

import cats.effect.Sync
import doobie.util.transactor.Transactor
import doobie.implicits._
import models.Exists
import repository.query.UserQueries

class UserExists[F[_] : Sync](tx: Transactor[F]) {
  def apply(uuid: String): F[Exists] = {
    UserQueries
      .userExists(uuid)
      .unique
      .map(Exists)
      .transact(tx)
  }

}