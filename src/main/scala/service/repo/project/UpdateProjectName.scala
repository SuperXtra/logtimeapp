package service.repo.project

import cats.effect.Sync
import data.Queries
import doobie.implicits._
import doobie.util.transactor.Transactor


class UpdateProjectName[F[+_]: Sync](tx: Transactor[F]) {

  def apply(oldName: String, newName: String, userId: Long): F[Int] =
    Queries
      .Project
      .changeName(oldName,newName,userId)
      .run
      .transact(tx)
}
