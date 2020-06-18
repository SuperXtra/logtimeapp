package service.project

import cats.data.EitherT
import cats.effect.Sync
import error._
import models.UserId
import repository.project._
import repository.user.GetUserByUUID


class UpdateProject[F[+_]: Sync](userId: GetUserByUUID[F],
                                 updateProjectName: UpdateProjectName[F]) {

  def apply(oldName: String, newProjectName: String, uuid: String): F[Either[LogTimeAppError, Unit]] = (for {
    userId <- getExistingUserId(uuid)
    result <- changeProjectName(oldName, newProjectName, userId)
  } yield result).value

  private def getExistingUserId(userIdentification: String): EitherT[F, UserNotFound.type, UserId] = {
    EitherT.fromOptionF(userId(userIdentification), UserNotFound )
  }

  private def changeProjectName(oldProjectName: String, projectName: String, userId: UserId): EitherT[F, LogTimeAppError, Unit] =
    EitherT(updateProjectName(oldProjectName, projectName, userId))
}