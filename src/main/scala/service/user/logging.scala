package service.user

import akka.event.MarkerLoggingAdapter
import cats.effect._
import cats.implicits._
import models.UserId
import models.model.User

object logging {


  def checkingWhetherUserExists[F[_] : Sync](uuid: String)
                                            (implicit logger: MarkerLoggingAdapter): Unit =
    logger.info(s"[SERVICE][USER] Checking whether user with uuid: $uuid exists")


  def fetchedUser[F[_] : Sync](user: User)
                                            (implicit logger: MarkerLoggingAdapter): Unit =
    logger.info(s"[SERVICE][USER] Fetched user with id: ${user.userId.value}")

  def createdUserWithId[F[_] : Sync](id: UserId)
                                  (implicit logger: MarkerLoggingAdapter): Unit =
    logger.info(s"[SERVICE][USER] Created user with id: ${id.value}")
}