package repository.project

import cats.effect.Sync
import error._
import models._
import repository.query.ProjectQueries
import scala.concurrent._
import ExecutionContext.Implicits.global
import scala.util._
import cats.implicits._
import models.model.Project
import slick.jdbc.PostgresProfile.api._


class InsertProject[F[+_] : Sync] {
  def apply(projectName: String, userId: UserId): DBIOAction[Either[LogTimeAppError, ProjectId], NoStream, Effect.Write with Effect] = {
    ProjectQueries
      .insert(projectName, userId)
      .asTry.flatMap {
      case Failure(_) => DBIO.successful(ProjectNotCreated.asLeft)
      case Success(value: Project) => DBIO.successful(value.id.asRight)
    }
  }
}