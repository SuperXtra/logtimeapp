package repository.user

import java.util.UUID

import cats.effect.Sync
import doobie.util.transactor.Transactor
import doobie.implicits._
import repository.query.UserQueries

class CreateUser[F[_]: Sync](tx: Transactor[F]) {

  def apply(uuid: String): F[Option[Int]] = {
    UserQueries
      .insertUser(uuid)
      .option
      .transact(tx)
  }

}
