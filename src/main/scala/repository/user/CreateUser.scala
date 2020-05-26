package repository.user

import java.util.UUID

import cats.effect.Sync
import doobie.util.transactor.Transactor
import doobie.implicits._
import repository.queries.User

class CreateUser[F[_]: Sync](tx: Transactor[F]) {

  def apply(): F[Option[Long]] = {
    User
      .insertUser(UUID.randomUUID().toString)
      .transact(tx)
  }

}
