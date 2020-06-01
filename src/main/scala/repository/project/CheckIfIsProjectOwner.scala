package repository.project

import cats.effect.Sync
import doobie.util.transactor.Transactor
import errorMessages._
import repository.query.ProjectQueries
import doobie.implicits._
import cats.implicits._

class CheckIfIsProjectOwner[F[+_] : Sync](tx: Transactor[F]) {

  def apply(userId: Int, projectName: String): F[Either[AppBusinessError, Boolean]] =
    ProjectQueries
      .checkIfIsOwner(userId, projectName)
      .unique
      .map {
        case true => true.asRight
        case false => ProjectDeleteUnsuccessfulUserIsNotTheOwner().asLeft
      }
      .transact(tx)

}
