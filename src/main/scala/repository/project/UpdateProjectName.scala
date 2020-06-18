package repository.project

import cats.effect.Sync
import doobie.implicits._
import doobie.util.transactor.Transactor
import repository.query.ProjectQueries
import cats.implicits._
import doobie.postgres.sqlstate
import error.{LogTimeAppError, ProjectNameExists, ProjectUpdateUnsuccessful}
import models.UserId


class UpdateProjectName[F[+_] : Sync](tx: Transactor[F]) {

  def apply(oldName: String, newName: String, userId: UserId): F[Either[LogTimeAppError, Unit]] =
    ProjectQueries
      .changeName(oldName, newName, userId)
      .run
      .transact(tx)
    .attemptSomeSqlState {
    case sqlstate.class23.UNIQUE_VIOLATION => ProjectNameExists
  }.map {
      case Left(error) => error.asLeft
      case Right(updateCount) => updateCount match {
        case 0 => ProjectUpdateUnsuccessful.asLeft
        case 1 => ().asRight
      }
    }
}
