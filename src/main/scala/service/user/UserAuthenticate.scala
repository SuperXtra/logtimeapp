package service.user

import cats.effect.Sync
import cats.implicits._
import errorMessages.AppBusinessError
import repository.user.{CreateUser, UserById, UserExists}

class UserAuthenticate[F[+_] : Sync](exists: UserExists[F]) {
  def apply(uuid: String): F[Boolean] = exists(uuid)
}
