package repository.report

import cats.effect.Sync
import doobie.util.transactor.Transactor
import errorMessages.{AppBusinessError, ReportCouldNotBeGenerated}
import models.request.{MainReport, ReportBodyWithParamsRequest}
import models.responses.{ReportFromDb, OverallStatisticsReport}
import repository.query.{GenerateReportQueries, StatisticsReportQuery}
import doobie.implicits._

class DetailedReport[F[_] : Sync](tx: Transactor[F]) {
  def apply(req: MainReport): F[Either[AppBusinessError, OverallStatisticsReport]] = {
    StatisticsReportQuery(req)
      .unique
      .transact(tx)
      .attemptSomeSqlState {
        case _ => ReportCouldNotBeGenerated()
      }
  }

}
