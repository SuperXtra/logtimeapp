package service.repo.user

import cats.effect.Sync
import data.Queries
import doobie.implicits._
import doobie.util.transactor.Transactor

class GetExistingUserId[F[_]: Sync](tx: Transactor[F]) {
  def apply(userIdentification: String): F[Option[Long]] = {
    Queries
      .User
      .getUserId(userIdentification)
      .transact(tx)
  }
}
