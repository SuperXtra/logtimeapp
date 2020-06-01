package service.user

import java.util.UUID

import cats.data.EitherT
import cats.effect.{IO, Sync}
import errorMessages.{AppBusinessError, CannotCreateUserWithGeneratedUUID, UserNotFound}
import models.model.User
import repository.user.{CreateUser, UserById}

class UserCreate[F[+_] : Sync](getNewUser: UserById[F],
                               create: CreateUser[F]) {

  def apply(): F[Either[AppBusinessError, User]] = (
    for {
      id <- createUser()
      user <- getExistingUserById(id)
    } yield user
    ).value


  private def createUser(): EitherT[F, AppBusinessError, Int] = {
    EitherT.fromOptionF(create(UUID.randomUUID().toString), CannotCreateUserWithGeneratedUUID())
  }

  private def getExistingUserById(id: Int): EitherT[F, AppBusinessError, User] = {
    EitherT.fromOptionF(getNewUser(id), UserNotFound())
  }

}
