package service.user

import cats.effect.Sync
import cats.implicits._
import error.AppError
import repository.user.{CreateUser, UserById, UserExists}

class AuthenticateUser[F[+_] : Sync](exists: UserExists[F]) {
  def apply(uuid: String): F[Boolean] ={
    exists(uuid)
  }

}
