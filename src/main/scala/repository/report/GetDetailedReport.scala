package repository.report

import cats.effect.Sync
import doobie.util.transactor.Transactor
import error.{LogTimeAppError, ReportCouldNotBeGenerated}
import models.request.{MainReport, ReportBodyWithParamsRequest}
import models.reports.{ReportFromDb, OverallStatisticsReport}
import repository.query.{GenerateReportQueries, StatisticsReportQuery}
import doobie.implicits._

class GetDetailedReport[F[_] : Sync](tx: Transactor[F]) {
  def apply(req: MainReport): F[Either[LogTimeAppError, OverallStatisticsReport]] = {
    StatisticsReportQuery(req)
      .unique
      .transact(tx)
      .attemptSomeSqlState {
        case _ => ReportCouldNotBeGenerated
      }
  }

}
