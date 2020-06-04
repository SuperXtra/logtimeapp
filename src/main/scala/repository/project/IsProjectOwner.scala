package repository.project

import cats.effect.Sync
import doobie.util.transactor.Transactor
import error._
import repository.query.ProjectQueries
import doobie.implicits._
import cats.implicits._

class IsProjectOwner[F[+_] : Sync](tx: Transactor[F]) {

  def apply(userId: Int, projectName: String): F[Either[LogTimeAppError, Boolean]] =
    ProjectQueries
      .checkIfUserIsOwner(userId, projectName)
      .unique
      .transact(tx)
      .map {
        case true => true.asRight
        case false => ProjectDeleteUnsuccessfulUserIsNotTheOwner.asLeft
      }
}