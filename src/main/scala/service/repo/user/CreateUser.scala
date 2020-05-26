package service.repo.user

import java.util.UUID

import cats.effect.Sync
import data.Queries
import doobie.util.transactor.Transactor
import doobie.implicits._

class CreateUser[F[_]: Sync](tx: Transactor[F]) {

  def apply(): F[Option[Long]] = {
    Queries
      .User
      .insertUser(UUID.randomUUID().toString)
      .transact(tx)
  }

}
