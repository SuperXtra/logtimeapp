package service.report

import cats.data.EitherT
import cats.effect.Sync
import error.{AppError, ReportCouldNotBeGenerated}
import models.request.ReportRequest
import models.responses.FinalReport
import repository.project.FindProjectById
import repository.report.Report
import repository.task.GetProjectTasks

class ProjectWithTasks[F[+_] : Sync](getReport: Report[F]) {

  //TODO add page and limit
  def apply(projectQuert: ReportRequest): F[Either[AppError, List[FinalReport]]] = {

    getReport(projectQuert)

  }
}
