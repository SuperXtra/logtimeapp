package repository.report

import cats.effect.Sync
import doobie.util.transactor.Transactor
import error.{LogTimeAppError, ReportCouldNotBeGenerated}
import models.request.{MainReport, ReportBodyWithParamsRequest}
import models.reports.{OverallStatisticsReport, ReportFromDb}
import repository.query.{GenerateReportQueries, StatisticsReportQuery}
import doobie.implicits._
import slick.jdbc.PostgresProfile.api._

import scala.concurrent._
import ExecutionContext.Implicits.global
import scala.util.{Failure, Success}
import cats.implicits._
import slick.jdbc.PostgresProfile

import scala.util.{Failure, Success}

class GetDetailedReport[F[_] : Sync] {
  def apply(req: MainReport): DBIOAction[Either[LogTimeAppError, OverallStatisticsReport], NoStream, PostgresProfile.api.Effect with Effect] = {
    StatisticsReportQuery(req)
      .head
      .asTry
      .flatMap {
        case Failure(_) => DBIO.successful(ReportCouldNotBeGenerated.asLeft)
        case Success(value) =>DBIO.successful(value.asRight)
      }
  }
}
