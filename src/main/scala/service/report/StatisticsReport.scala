package service.report

import cats.effect.Sync
import errorMessages.AppBusinessError
import models.request.MainReport
import models.responses.OverallStatisticsReport
import repository.report.DetailedReport


class StatisticsReport[F[+_] : Sync](getReport: DetailedReport[F]) {
  def apply(projectQuery: MainReport): F[Either[AppBusinessError, OverallStatisticsReport]] =
    getReport(projectQuery)

}
