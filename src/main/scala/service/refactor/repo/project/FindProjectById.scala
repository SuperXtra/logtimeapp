package service.refactor.repo.project

import cats.effect.Sync
import data.Entities._
import data._
import doobie.implicits._
import doobie.util.transactor.Transactor


class FindProjectById[F[+_]: Sync](tx: Transactor[F]) {
  def apply(projectName: String): F[Option[Project]] =
    Queries
      .Project
      .getProject(projectName)
      .transact(tx)

}
