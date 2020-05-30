package service.report

import cats.data.EitherT
import cats.effect.Sync
import error.{AppError, ReportCouldNotBeGenerated}
import models.request.ReportBodyWithParamsRequest
import models.responses.ReportFromDb
import repository.project.FindProjectById
import repository.report.Report
import repository.task.GetProjectTasks

class DetailReport[F[+_] : Sync](getReport: Report[F]) {

  //TODO add page and limit
  def apply(projectQuery: ReportBodyWithParamsRequest): F[Either[AppError, List[ReportFromDb]]] =
    getReport(projectQuery)
}
