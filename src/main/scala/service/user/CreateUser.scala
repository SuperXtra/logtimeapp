package service.user

import java.util.UUID

import cats.data.EitherT
import cats.effect.{IO, Sync}
import error.{LogTimeAppError, CannotCreateUserWithGeneratedUUID, UserNotFound}
import models.model.User
import repository.user.{InsertUser, GetUserById}

class CreateUser[F[+_] : Sync](getNewUser: GetUserById[F],
                               create: InsertUser[F]) {

  def apply: F[Either[LogTimeAppError, User]] = (
    for {
      id <- createUser
      user <- getExistingUserById(id)
    } yield user
    ).value

  private def createUser: EitherT[F, LogTimeAppError, Int] = {
    EitherT(create(UUID.randomUUID().toString))
  }

  private def getExistingUserById(id: Int): EitherT[F, LogTimeAppError, User] = {
    EitherT.fromOptionF(getNewUser(id), UserNotFound)
  }
}