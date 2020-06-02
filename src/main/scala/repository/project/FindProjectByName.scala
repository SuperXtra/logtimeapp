package repository.project

import cats.effect.Sync
import doobie.util.transactor.Transactor
import models.model.Project
import repository.query.ProjectQueries
import doobie.implicits._
import errorMessages.{AppBusinessError, ProjectNotFound}
import cats.implicits._

class FindProjectByName[F[+_] : Sync](tx: Transactor[F]) {
  def apply(projectName: String): F[Either[AppBusinessError, Project]] =
    ProjectQueries
      .getActiveProjectByName(projectName)
      .option
      .map {
        case Some(project) => project.asRight
        case None => ProjectNotFound().asLeft
      }
      .transact(tx)

}
