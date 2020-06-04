package service.user

import cats.effect.Sync
import cats.implicits._
import error.LogTimeAppError
import repository.user.{InsertUser, GetUserById, UserExists}

class AuthenticateUser[F[+_] : Sync](exists: UserExists[F]) {
  def apply(uuid: String): F[Boolean] = exists(uuid)
}
