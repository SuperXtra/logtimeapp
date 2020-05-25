package service

import data.Queries
import java.util.UUID
import cats.data.EitherT
import cats.effect.{ContextShift, IO}
import data.Entities.User
import doobie.implicits._
import doobie.util.transactor.Transactor.Aux
import error._


class UserService(con: Aux[IO, Unit])(implicit val contextShift: ContextShift[IO]) {

  def createNewUser(): IO[Either[AppError, User]] = (
    for {
    id <- createUser()
    user <- getExistingUserById(id)
  } yield user
    ).value


  private def createUser(): EitherT[IO, AppError, Long] = {
    EitherT.fromOptionF(Queries.User.insertUser(UUID.randomUUID().toString).transact(con), CannotCreateUserWithGeneratedUUID)
  }

  private def getExistingUserById(id: Long): EitherT[IO, AppError, User] = {
    EitherT.fromOptionF(Queries.User.selectByUserIdentity(id).transact(con), UserNotFound)
  }

  def getExistingUserId(uuid: String): EitherT[IO, AppError, Long] =
    EitherT.fromOptionF(Queries.User.getUserId(uuid).transact(con), UserNotFound)

}
