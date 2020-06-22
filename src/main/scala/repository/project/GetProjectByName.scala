package repository.project

import cats.effect.Sync
import doobie.util.transactor.Transactor
import models.model.Project
import repository.query.ProjectQueries
import doobie.implicits._
import error.{LogTimeAppError, ProjectNotCreated, ProjectNotFound}
import cats.implicits._
import slick.jdbc.PostgresProfile.api._
import scala.concurrent._
import ExecutionContext.Implicits.global

import scala.util.{Failure, Success}

class GetProjectByName[F[+_] : Sync] {
  def apply(projectName: String): DBIOAction[Either[LogTimeAppError, Project], NoStream, Effect.Read with Effect] =
    ProjectQueries
      .getActiveProjectByName(projectName)
    .headOption
    .asTry
    .flatMap {
      case Failure(_) => DBIO.successful(ProjectNotFound.asLeft)
      case Success(value: Option[Project]) => value match {
        case Some(value) => DBIO.successful(value.asRight)
        case None =>DBIO.successful(ProjectNotFound.asLeft)
      }
    }
}