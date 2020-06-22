package repository.project

import cats.effect.Sync
import repository.query.ProjectQueries
import cats.implicits._
import error.{LogTimeAppError, ProjectNameExists, ProjectUpdateUnsuccessful}
import models.UserId
import slick.jdbc.PostgresProfile.api._
import scala.concurrent._
import ExecutionContext.Implicits.global
import scala.util.{Failure, Success}


class UpdateProjectName[F[+_] : Sync] {
  def apply(oldName: String, newName: String, userId: UserId): DBIOAction[Either[LogTimeAppError, Unit], NoStream, Effect.Write with Effect] =
    ProjectQueries
      .changeName(oldName, newName, userId)
      .asTry
      .flatMap {
        case Failure(_) => DBIO.successful(ProjectNameExists.asLeft)
        case Success(value: Int) => value match {
          case 0 => DBIO.successful(ProjectUpdateUnsuccessful.asLeft)
          case 1 => DBIO.successful(().asRight)
        }
      }
}