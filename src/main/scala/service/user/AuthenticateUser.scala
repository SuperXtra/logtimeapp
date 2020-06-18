package service.user

import cats.effect.Sync
import cats.implicits._
import error.LogTimeAppError
import models.Exists
import repository.user.{GetUserById, InsertUser, UserExists}

class AuthenticateUser[F[+_] : Sync](exists: UserExists[F]) {
  def apply(uuid: String): F[Exists] = exists(uuid)
}
