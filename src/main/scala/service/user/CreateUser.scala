package service.user

import java.util.UUID

import cats.data.EitherT
import cats.effect.{IO, Sync}
import error.{CannotCreateUserWithGeneratedUUID, LogTimeAppError, UserNotFound}
import models.UserId
import models.model.User
import repository.user.{GetUserById, InsertUser}

class CreateUser[F[+_] : Sync](getNewUser: GetUserById[F],
                               create: InsertUser[F]) {

  def apply: F[Either[LogTimeAppError, User]] = (
    for {
      id <- createUser
      user <- getExistingUserById(id)
    } yield user
    ).value

  private def createUser: EitherT[F, CannotCreateUserWithGeneratedUUID.type, UserId] = {
    EitherT(create(UUID.randomUUID().toString))
  }

  private def getExistingUserById(id: UserId): EitherT[F, LogTimeAppError, User] = {
    EitherT.fromOptionF(getNewUser(id), UserNotFound)
  }
}