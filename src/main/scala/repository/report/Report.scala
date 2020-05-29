package repository.report

import cats.effect.Sync
import doobie.implicits._
import doobie.util.transactor.Transactor
import error.{AppError, ReportCouldNotBeGenerated}
import models.request.ReportRequest
import models.responses.FinalReport
import repository.queries.Report

class Report[F[_]: Sync](tx: Transactor[F]) {

  def apply(projectQuert: ReportRequest): F[Either[AppError, List[FinalReport]]] = {
    Report(projectQuert)
      .to[List]
      .transact(tx)
      .attemptSomeSqlState{
        case _ => ReportCouldNotBeGenerated()
    }
  }

}
