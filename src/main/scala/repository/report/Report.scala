package repository.report

import cats.effect.Sync
import doobie.implicits._
import doobie.util.transactor.Transactor
import errorMessages.{AppBusinessError, ReportCouldNotBeGenerated}
import models.request.ReportBodyWithParamsRequest
import models.responses.ReportFromDb
import repository.query.GenerateReportQueries

class Report[F[_] : Sync](tx: Transactor[F]) {

  def apply(projectQuery: ReportBodyWithParamsRequest): F[Either[AppBusinessError, List[ReportFromDb]]] = {
    GenerateReportQueries(projectQuery)
      .to[List]
      .transact(tx)
      .attemptSomeSqlState {
        case _ => ReportCouldNotBeGenerated()
      }
  }

}
