package service.project

import akka.event.MarkerLoggingAdapter
import utils.EitherT
import cats.effect.{ContextShift, IO, Sync}
import db.DatabaseContext
import doobie.util.ExecutionContexts
import models._
import error._
import repository.project.InsertProject
import repository.user.GetUserByUUID
import slick.jdbc.PostgresProfile.api._
import scala.concurrent._
import ExecutionContext.Implicits.global
import db.RunDBIOAction._

class CreateProject[F[+_] : Sync](
                                   getUserId: GetUserByUUID[F],
                                   createProject: InsertProject[F]
                                 )
                                 (implicit db: Database,
                                  logger: MarkerLoggingAdapter,
                                  ec: ContextShift[IO])  {

  def apply(projectName: String, uuid: String): IO[Either[LogTimeAppError, ProjectId]] = {
      (for {
        userId <- getExistingUserId(uuid)
        _ = logging.foundUserWithId(userId.userId, uuid)
        projectId <- insertProject(projectName, userId.userId)
        _ = logging.projectCreated(projectId, projectName, userId.userId)
      } yield projectId).value.transactionally.exec
  }

  def insertProject(projectName: String, userId: UserId) =
    EitherT(createProject(projectName, userId))

  def getExistingUserId(uuid: String) =
    EitherT(getUserId(uuid))
}