package service.repo.user

import cats.effect.Sync
import data.Entities.User
import data.{Entities, Queries}
import doobie.util.transactor.Transactor
import doobie.implicits._

class UserById[F[_]: Sync](tx: Transactor[F]) {

  def apply(id: Long): F[Option[User]] = {
    Queries.User.selectByUserIdentity(id).transact(tx)
  }

}
