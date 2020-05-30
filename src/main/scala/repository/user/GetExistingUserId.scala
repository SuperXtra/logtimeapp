package repository.user

import cats.effect.Sync
import doobie.implicits._
import doobie.util.transactor.Transactor
import repository.query.UserQueries

class GetExistingUserId[F[_] : Sync](tx: Transactor[F]) {
  def apply(userIdentification: String): F[Option[Int]] = {
    UserQueries
      .getUserId(userIdentification)
      .option
      .transact(tx)
  }
}
