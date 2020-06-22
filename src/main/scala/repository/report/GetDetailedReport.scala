package repository.report

import cats.effect.Sync
import error.{LogTimeAppError, ReportCouldNotBeGenerated}
import models.request._
import models.reports._
import repository.query._
import slick.jdbc.PostgresProfile.api._
import scala.concurrent._
import ExecutionContext.Implicits.global
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
