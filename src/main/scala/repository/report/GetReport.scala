package repository.report

import cats.effect.Sync
import doobie.util.transactor.Transactor
import error._
import models.request.ReportBodyWithParamsRequest
import repository.query.GenerateReportQueries
import slick.jdbc.PostgresProfile.api._

import scala.util.{Failure, Success}
import cats.implicits._
import models.reports.ReportFromDb

import scala.concurrent._
import ExecutionContext.Implicits.global

class GetReport[F[_] : Sync] {

  def apply(projectQuery: ReportBodyWithParamsRequest): DBIOAction[Either[LogTimeAppError, List[ReportFromDb]], NoStream, Effect] = {
    GenerateReportQueries(projectQuery)
      .asTry
      .flatMap {
        case Failure(_) => DBIO.successful(ReportCouldNotBeGenerated.asLeft)
        case Success(seq) =>DBIO.successful(seq.toList.asRight)
      }
  }

}
