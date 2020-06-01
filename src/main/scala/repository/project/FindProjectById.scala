package repository.project

import cats.effect.Sync
import doobie.util.transactor.Transactor
import models.model.Project
import repository.query.ProjectQueries
import doobie.implicits._

class FindProjectById[F[+_]: Sync](tx: Transactor[F]) {
  def apply(projectName: String): F[Option[Project]] =
    ProjectQueries
      .getActiveProjectById(projectName)
      .option
      .transact(tx)

}
