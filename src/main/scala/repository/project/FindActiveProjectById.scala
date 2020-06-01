package repository.project

import cats.effect.Sync
import models.model.Project
import doobie.implicits._
import doobie.util.transactor.Transactor
import errorMessages.{AppBusinessError, ProjectNotFound}
import repository.query.ProjectQueries
import cats.implicits._


class FindActiveProjectById[F[+_] : Sync](tx: Transactor[F]) {
  def apply(projectName: String): F[Either[AppBusinessError, Project]] =
    ProjectQueries
      .getActiveProjectById(projectName)
      .option
      .map {
        case Some(project) => project.asRight
        case None => ProjectNotFound().asLeft
      }.transact(tx)
}
