package repository.report

import cats.effect.Sync
import doobie.implicits._
import doobie.util.transactor.Transactor
import error.{LogTimeAppError, ReportCouldNotBeGenerated}
import models.request.ReportBodyWithParamsRequest
import models.reports.ReportFromDb
import repository.query.GenerateReportQueries

class GetReport[F[_] : Sync](tx: Transactor[F]) {

  def apply(projectQuery: ReportBodyWithParamsRequest): F[Either[LogTimeAppError, List[ReportFromDb]]] = {
    GenerateReportQueries(projectQuery)
      .to[List]
      .transact(tx)
      .attemptSomeSqlState {
        case _ => ReportCouldNotBeGenerated
      }
  }

}
