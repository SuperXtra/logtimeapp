package repository.project

import cats.effect.Sync
import doobie.implicits._
import doobie.util.transactor.Transactor
import repository.queries.Project


class UpdateProjectName[F[+_]: Sync](tx: Transactor[F]) {

  def apply(oldName: String, newName: String, userId: Long): F[Int] =
    Project
      .changeName(oldName,newName,userId)
      .run
      .transact(tx)
}
