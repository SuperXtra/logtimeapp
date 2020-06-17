package service.report

import cats.effect.Sync
import error.LogTimeAppError
import models.request.MainReport
import models.reports.OverallStatisticsReport
import repository.report.GetDetailedReport

class GetStatisticsReport[F[+_] : Sync](getStatisticsReport: GetDetailedReport[F]) {
  def apply(projectQuery: MainReport): F[Either[LogTimeAppError, OverallStatisticsReport]] =
    getStatisticsReport(projectQuery)
}