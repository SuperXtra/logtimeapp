package repository.project

import cats.effect.Sync
import doobie.util.transactor.Transactor
import models.model.Project
import repository.query.ProjectQueries
import doobie.implicits._
import error.{LogTimeAppError, ProjectNotFound}
import cats.implicits._

class GetProjectByName[F[+_] : Sync](tx: Transactor[F]) {
  def apply(projectName: String): F[Option[Project]] =
    ProjectQueries
      .getActiveProjectByName(projectName)
      .option
      .transact(tx)
}