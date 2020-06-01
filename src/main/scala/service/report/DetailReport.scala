package service.report

import java.time.LocalDateTime

import cats.data.EitherT
import cats.effect.Sync
import errorMessages.{AppBusinessError, ReportCouldNotBeGenerated}
import models.request.ReportBodyWithParamsRequest
import models.responses._
import repository.project.FindActiveProjectById
import repository.report.Report
import repository.task.GetProjectTasks

class DetailReport[F[+_] : Sync](getReport: Report[F]) {

  //TODO add page and limit
  def apply(projectQuery: ReportBodyWithParamsRequest): F[Either[AppBusinessError, Seq[DetailReportResponse]]] =
    generateReport(projectQuery)


  private def generateReport(projectQuery: ReportBodyWithParamsRequest): F[Either[AppBusinessError, Seq[DetailReportResponse]]] = {
    EitherT(getReport(projectQuery)).map(x => groupByOrdered(x)(x => (x.project_name, x.project_create_time)).map {
      case ((projectName, projectCreatedTime), list) => {

        val reportTasks = list.map(task =>
          ReportTask(
            task.task_create_time, task.task_description,
            task.start_time, task.end_time, task.duration,
            task.volume, task.comment))

        DetailReportResponse(projectName, projectCreatedTime, reportTasks.toList)
      }
    }).value
  }


  private def groupByOrdered[A, K](xs: collection.Seq[A])(f: A => K): collection.Seq[(K, collection.Seq[A])] = {
    val m = collection.mutable.LinkedHashMap.empty[K, collection.Seq[A]].withDefault(_ => new collection.mutable.ArrayBuffer[A])
    xs.foreach { x =>
      val k = f(x)
      m(k) = m(k) :+ x
    }
    m.toSeq
  }

}






