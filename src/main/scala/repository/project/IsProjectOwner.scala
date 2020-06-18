package repository.project

import cats.effect.Sync
import doobie.util.transactor.Transactor
import error._
import repository.query.ProjectQueries
import doobie.implicits._
import cats.implicits._
import models.{IsOwner, UserId}

class IsProjectOwner[F[+_] : Sync](tx: Transactor[F]) {

  def apply(userId: UserId, projectName: String): F[Either[LogTimeAppError, IsOwner]] =
    ProjectQueries
      .checkIfUserIsOwner(userId, projectName)
      .unique
      .transact(tx)
      .map {
        case true => IsOwner(true).asRight
        case false => ProjectDeleteUnsuccessfulUserIsNotTheOwner.asLeft
      }
}