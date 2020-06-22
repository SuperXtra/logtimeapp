package service.user

import java.util.UUID

import akka.event.MarkerLoggingAdapter
import cats.effect._
import models._
import repository.user.{GetUserById, InsertUser}
import scala.concurrent._
import ExecutionContext.Implicits.global
import utils.EitherT
import slick.jdbc.PostgresProfile.api._
import db.RunDBIOAction._

class CreateUser[F[+_] : Sync](getNewUser: GetUserById[F],
                               create: InsertUser[F])
                              (implicit db: Database,
                               logger: MarkerLoggingAdapter,
                               ec: ContextShift[IO]) {
  def apply =
    (
      for {
        id <- createUser
        _ = logging.createdUserWithId(id)
        user <- getExistingUserById(id)
        _ = logging.fetchedUser(user)
      } yield user).value.exec

  private def createUser =
    EitherT(create(UUID.randomUUID().toString))

  private def getExistingUserById(id: UserId) =
    EitherT(getNewUser(id))
}