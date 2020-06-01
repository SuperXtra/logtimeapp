package repository.project

import cats.effect.Sync
import doobie.implicits._
import doobie.util.transactor.Transactor
import repository.query.ProjectQueries
import cats.implicits._
import errorMessages.{AppBusinessError, ProjectUpdateUnsuccessful}


class UpdateProjectName[F[+_] : Sync](tx: Transactor[F]) {

  def apply(oldName: String, newName: String, userId: Long): F[Either[AppBusinessError, Unit]] =
    ProjectQueries
      .changeName(oldName, newName, userId)
      .run
      .map {
        case 0 => ProjectUpdateUnsuccessful().asLeft
        case 1 => ().asRight
      }.transact(tx)
}
