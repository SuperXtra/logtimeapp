package repository.project

import cats.effect.Sync
import doobie.implicits._
import doobie.util.transactor.Transactor
import repository.query.ProjectQueries


class UpdateProjectName[F[+_]: Sync](tx: Transactor[F]) {

  def apply(oldName: String, newName: String, userId: Long): F[Int] =
    ProjectQueries
      .changeName(oldName,newName,userId)
      .run
      .transact(tx)
}
