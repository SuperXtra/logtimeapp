package service.user

import java.util.UUID

import cats.data.EitherT
import cats.effect.{IO, Sync}
import error.{AppError, CannotCreateUserWithGeneratedUUID, UserNotFound}
import models.model.User
import repository.user.{CreateUser, UserById}

class UserCreate[F[+_] : Sync](getNewUser: UserById[F],
                               create: CreateUser[F]) {

  def apply(): F[Either[AppError, User]] = (
    for {
      id <- createUser()
      user <- getExistingUserById(id)
    } yield user
    ).value


  private def createUser(): EitherT[F, AppError, Int] = {
    EitherT.fromOptionF(create(), CannotCreateUserWithGeneratedUUID())
  }

  private def getExistingUserById(id: Int): EitherT[F, AppError, User] = {
    EitherT.fromOptionF(getNewUser(id), UserNotFound())
  }

}
