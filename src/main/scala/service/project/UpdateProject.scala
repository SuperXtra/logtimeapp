package service.project

import akka.event.MarkerLoggingAdapter
import cats.effect.{ContextShift, IO, Sync}
import models.UserId
import repository.project._
import repository.user.GetUserByUUID
import slick.jdbc.PostgresProfile.api._
import utils.EitherT
import scala.concurrent._
import ExecutionContext.Implicits.global
import db.RunDBIOAction._

class UpdateProject[F[+_] : Sync](userId: GetUserByUUID[F],
                                  updateProjectName: UpdateProjectName[F])
                                 (implicit db: Database,
                                  logger: MarkerLoggingAdapter,
                                  ec: ContextShift[IO]) {

  def apply(oldName: String, newProjectName: String, uuid: String) =
      (for {
        user <- getExistingUserId(uuid)
        userId = user.userId
        _ = logging.foundUserWithId(userId, uuid)
        _ <- changeProjectName(oldName, newProjectName, userId)
        _ = logging.changedProjectName(oldName, newProjectName)
      } yield ()).value.transactionally.exec

  private def getExistingUserId(userIdentification: String) = {
    EitherT(userId(userIdentification))
  }

  private def changeProjectName(oldProjectName: String, projectName: String, userId: UserId) =
    EitherT(updateProjectName(oldProjectName, projectName, userId))
}