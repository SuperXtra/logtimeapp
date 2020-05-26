package service.serv.user

import java.util.UUID

import cats.data.EitherT
import cats.effect.{IO, Sync}
import data.Entities.User
import data.Queries
import error.{AppError, CannotCreateUserWithGeneratedUUID, UserNotFound}
import service.refactor.repo.user.UserById
import service.repo.user.{CreateUser, UserById}

class CreateNewUser [F[+_]: Sync](getNewUser: UserById[F],
                                  create: CreateUser[F]) {

  def apply(): F[Either[AppError, User]] = (
    for {
      id <- createUser()
      user <- getExistingUserById(id)
    } yield user
    ).value


  private def createUser(): EitherT[F, AppError, Long] = {
    EitherT.fromOptionF(create(), CannotCreateUserWithGeneratedUUID)
  }

  private def getExistingUserById(id: Long): EitherT[F, AppError, User] = {
    EitherT.fromOptionF(getNewUser(id), UserNotFound)
  }

//  private def getExistingUserId(uuid: String): EitherT[F, AppError, Long] =
//    EitherT.fromOptionF(existingUserId(uuid), UserNotFound)
//

}
