package service.project

import java.time.{ZonedDateTime, _}

import akka.event.MarkerLoggingAdapter
import cats.effect._
import error._
import models._
import repository.project._
import repository.user.GetUserByUUID
import scala.concurrent._
import ExecutionContext.Implicits.global
import slick.jdbc.PostgresProfile.api._
import utils.EitherT
import db.RunDBIOAction._


class DeactivateProject[F[+_] : Sync](
                                       userId: GetUserByUUID[F],
                                       deactivateProject: DeleteProjectWithTasks[F],
                                       findProject: GetProjectByName[F],
                                       checkIfOwner: IsProjectOwner[F]
                                     )  (implicit db: Database,
                                          logger: MarkerLoggingAdapter,
                                          ec: ContextShift[IO]) {

  def apply(projectName: String, uuid: String): IO[Either[LogTimeAppError, Unit]] = {
      (for {
        user <- getUserId(uuid)
        _ = logging.foundUserWithId(user.userId, uuid)
        deleteTime = ZonedDateTime.now(ZoneOffset.UTC)
        project <- findProjectById(projectName)
        _ = logging.foundProjectByName(projectName)
        verification <- verifyIfUserIsTheOwnerOfTheProject(user.userId, projectName)
        _ = logging.userIsOwner(verification, user.userId)
        _ <- deleteProjectWithTasks(user.userId, projectName, project.id, deleteTime)
        _ = logging.projectDeactivationSuccessful(user.userId, projectName, project.id, deleteTime)
      } yield ()).value.transactionally.exec
  }

  private def getUserId(userIdentification: String) =
    EitherT(userId(userIdentification))


  private def findProjectById(projectName: String)= {
    EitherT(findProject(projectName))
  }

  private def verifyIfUserIsTheOwnerOfTheProject(userId: UserId, projectName: String) = {
    EitherT(checkIfOwner(userId, projectName))
  }

  private def deleteProjectWithTasks(userId: UserId, projectName: String, projectId: ProjectId, deleteTime: ZonedDateTime) = {
    EitherT(deactivateProject(userId, projectName, projectId, deleteTime.toLocalDateTime))
  }
}