package repository.report

import cats.effect.Sync
import doobie.implicits._
import doobie.util.transactor.Transactor
import error.{AppError, ReportCouldNotBeGenerated}
import models.request.ReportBodyWithParamsRequest
import models.responses.ReportFromDb
import repository.query.GenerateReportQueries

class Report[F[_] : Sync](tx: Transactor[F]) {

  def apply(projectQuery: ReportBodyWithParamsRequest): F[Either[AppError, List[ReportFromDb]]] = {
    GenerateReportQueries(projectQuery)
      .to[List]
      .transact(tx)
      .attemptSomeSqlState {
        case _ => ReportCouldNotBeGenerated()
      }
  }

}
