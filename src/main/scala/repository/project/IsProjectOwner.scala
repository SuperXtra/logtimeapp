package repository.project

import cats.effect.Sync
import doobie.util.transactor.Transactor
import error._
import repository.query.ProjectQueries
import doobie.implicits._
import cats.implicits._
import models.{IsOwner, UserId}
import slick.jdbc.PostgresProfile.api._
import scala.concurrent._
import ExecutionContext.Implicits.global

import scala.util.{Failure, Success}

class IsProjectOwner[F[+_] : Sync] {

  def apply(userId: UserId, projectName: String): DBIOAction[Either[LogTimeAppError, IsOwner], NoStream, Effect.Read with Effect] =
    ProjectQueries
      .checkIfUserIsOwnerSlick(userId, projectName)
    .asTry
      .flatMap {
        case Failure(exception) => DBIO.successful(ProjectDeleteUnsuccessfulUserIsNotTheOwner.asLeft)
        case Success(value: Boolean) =>value match {
          case true => DBIO.successful(IsOwner(true).asRight)
          case false => DBIO.successful(ProjectDeleteUnsuccessfulUserIsNotTheOwner.asLeft)
        }
      }
}