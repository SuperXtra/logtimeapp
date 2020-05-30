package repository.project

import cats.effect.Sync
import models.model.Project
import doobie.implicits._
import doobie.util.transactor.Transactor
import repository.query.ProjectQueries


class FindProjectById[F[+_]: Sync](tx: Transactor[F]) {
  def apply(projectName: String): F[Option[Project]] =
    ProjectQueries
      .getProject(projectName)
      .option
      .transact(tx)

}
