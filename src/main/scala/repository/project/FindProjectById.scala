package repository.project

import cats.effect.Sync
import models.model.ProjectTb
import doobie.implicits._
import doobie.util.transactor.Transactor
import repository.queries.Project


class FindProjectById[F[+_]: Sync](tx: Transactor[F]) {
  def apply(projectName: String): F[Option[ProjectTb]] =
    Project
      .getProject(projectName)
      .transact(tx)

}
