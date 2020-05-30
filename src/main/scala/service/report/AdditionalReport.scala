package service.report

import cats.effect.Sync
import error.AppError
import models.request.MainReport
import models.responses.UserStatisticsReport
import repository.report.DetailedReport


class AdditionalReport[F[+_] : Sync](getReport: DetailedReport[F]) {
  def apply(projectQuery: MainReport): F[Either[AppError, List[UserStatisticsReport]]] =
    getReport(projectQuery)

}
