package service.report

import akka.event.MarkerLoggingAdapter
import cats.effect.{ContextShift, IO, Sync}
import error.LogTimeAppError
import models.request.MainReport
import models.reports.OverallStatisticsReport
import repository.report.GetDetailedReport
import slick.jdbc.PostgresProfile
import db.RunDBIOAction._
import slick.jdbc.PostgresProfile.api._

import scala.concurrent._
import ExecutionContext.Implicits.global

class GetStatisticsReport[F[+_] : Sync](getStatisticsReport: GetDetailedReport[F])
                                       (implicit db: Database,
                                        logger: MarkerLoggingAdapter,
                                        ec: ContextShift[IO]) {
  def apply(projectQuery: MainReport): IO[Either[LogTimeAppError, OverallStatisticsReport]] = {
    logging.generatingStatisticsReport(projectQuery)
    getStatisticsReport(projectQuery).exec
  }
}