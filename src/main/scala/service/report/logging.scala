package service.report

import akka.event.MarkerLoggingAdapter
import cats.effect._
import cats.implicits._
import models.request.{MainReport, ReportBodyWithParamsRequest}

object logging {
  def generatingReportForData[F[_] : Sync](data: ReportBodyWithParamsRequest)
                                          (implicit logger: MarkerLoggingAdapter): Unit =
    logger.info(s"[SERVICE][REPORT] Generating report for ids:  ${data.params.ids.getOrElse("all reports - not specified")} within dates: ${data.params.since.getOrElse("01.01.1990")} and ${data.params.upTo.getOrElse("01.01.2100")}")

  def generatingProjectReport[F[_] : Sync](projectName: String)
                                          (implicit logger: MarkerLoggingAdapter): Unit =
    logger.info(s"[SERVICE][REPORT] Generating report for project with name:  $projectName")

  def generatingStatisticsReport[F[_] : Sync](query: MainReport)
                                             (implicit logger: MarkerLoggingAdapter): Unit =
    logger.info(s"[SERVICE][REPORT] Generating statistics report for users: ${query.userUUIDs.getOrElse("all users - not specified")} within dates ${query.from.getOrElse("01.01.1990")} and ${query.to.getOrElse("01.01.2100")}")
}