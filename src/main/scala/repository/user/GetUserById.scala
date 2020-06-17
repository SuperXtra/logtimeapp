package repository.user

import cats.effect.Sync
import doobie.util.transactor.Transactor
import doobie.implicits._
import models.model.User
import repository.query.UserQueries

class GetUserById[F[_]: Sync](tx: Transactor[F]) {
  def apply(id: Int): F[Option[User]] = {
    UserQueries
      .getUserById(id)
      .option
      .transact(tx)
  }
}
