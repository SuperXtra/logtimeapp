package repository.report

import cats.effect.Sync
import doobie.util.transactor.Transactor
import error.{AppError, ReportCouldNotBeGenerated}
import models.request.{MainReport, ReportBodyWithParamsRequest}
import models.responses.{ReportFromDb, UserStatisticsReport}
import repository.query.{GenerateReportQueries, StatisticsReportQuery}
import doobie.implicits._

class DetailedReport[F[_] : Sync](tx: Transactor[F]) {
  def apply(req: MainReport): F[Either[AppError, List[UserStatisticsReport]]] = {
    StatisticsReportQuery(req)
      .to[List]
      .transact(tx)
      .attemptSomeSqlState {
        case _ => ReportCouldNotBeGenerated()
      }
  }

}
