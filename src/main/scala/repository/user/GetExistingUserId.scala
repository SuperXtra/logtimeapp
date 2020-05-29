package repository.user

import cats.effect.Sync
import doobie.implicits._
import doobie.util.transactor.Transactor
import repository.queries.User

class GetExistingUserId[F[_] : Sync](tx: Transactor[F]) {
  def apply(userIdentification: String): F[Option[Int]] = {
    User
      .getUserId(userIdentification)
      .option
      .transact(tx)
  }
}
