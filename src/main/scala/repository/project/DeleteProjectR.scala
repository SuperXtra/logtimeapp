package repository.project

import java.time.ZonedDateTime

import cats.effect._
import doobie.implicits._
import doobie.util.transactor.Transactor
import error._
import repository.queries.Project

class DeleteProjectR[F[+_]: Sync](tx: Transactor[F]) {

  def apply(userId: Long, projectName: String, timeZoneUTC: ZonedDateTime): F[Either[AppError, Int]] =
    Project
      .deleteProject(userId,projectName,timeZoneUTC)
      .run
      .transact(tx).attemptSomeSqlState(x=>ProjectDeleteUnsuccessful(detailErrorMessage = x.value))
}
