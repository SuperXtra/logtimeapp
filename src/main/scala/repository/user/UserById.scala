package repository.user

import cats.effect.Sync
import doobie.util.transactor.Transactor
import doobie.implicits._
import models.model.UserTb
import repository.queries.User

class UserById[F[_]: Sync](tx: Transactor[F]) {
  def apply(id: Int): F[Option[UserTb]] = {
    User
      .selectByUserIdentity(id)
      .option
      .transact(tx)
  }

}
