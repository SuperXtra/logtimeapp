package repository.user

import cats.effect.Sync
import doobie.implicits._
import doobie.util.transactor.Transactor
import repository.query.UserQueries

class GetUserByUUID[F[_] : Sync](tx: Transactor[F]) {
  def apply(userIdentification: String): F[Option[Int]] = {
    UserQueries
      .getUserIdByUUID(userIdentification)
      .option
      .transact(tx)
  }
}
