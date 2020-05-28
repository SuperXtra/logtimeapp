package service.user

import java.util.UUID

import cats.data.EitherT
import cats.effect.{IO, Sync}
import error.{AppError, CannotCreateUserWithGeneratedUUID, UserNotFound}
import models.model.UserTb
import repository.user.{CreateUser, UserById}

class CreateNewUser[F[+_] : Sync](getNewUser: UserById[F],
                                  create: CreateUser[F]) {

  def apply(): F[Either[AppError, UserTb]] = (
    for {
      id <- createUser()
      user <- getExistingUserById(id)
    } yield user
    ).value


  private def createUser(): EitherT[F, AppError, Int] = {
    EitherT.fromOptionF(create(), CannotCreateUserWithGeneratedUUID())
  }

  private def getExistingUserById(id: Int): EitherT[F, AppError, UserTb] = {
    EitherT.fromOptionF(getNewUser(id), UserNotFound())
  }

  //  private def getExistingUserId(uuid: String): EitherT[F, AppError, Long] =
  //    EitherT.fromOptionF(existingUserId(uuid), UserNotFound)
  //

}
