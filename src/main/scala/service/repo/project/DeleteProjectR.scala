package service.repo.project

import java.time.ZonedDateTime

import cats.effect._
import data.Queries
import doobie.implicits._
import doobie.util.transactor.Transactor
import error._

class DeleteProjectR[F[+_]: Sync](tx: Transactor[F]) {

  def apply(userId: Long, projectName: String, timeZoneUTC: ZonedDateTime): F[Either[AppError, Int]] =
    Queries
      .Project
      .deleteProject(userId,projectName,timeZoneUTC)
      .run
      .transact(tx).attemptSomeSqlState(_=>DeleteProjectUnsuccessful)
}
