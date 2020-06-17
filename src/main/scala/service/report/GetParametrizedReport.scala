package service.report

import java.time.LocalDateTime

import cats.data.EitherT
import cats.effect.Sync
import error.{LogTimeAppError, ReportCouldNotBeGenerated}
import models.request.ReportBodyWithParamsRequest
import models.reports._
import repository.report.GetReport
import repository.task.GetProjectTasks

class GetParametrizedReport[F[+_] : Sync](getReport: GetReport[F]) {

  def apply(projectQuery: ReportBodyWithParamsRequest): F[Either[LogTimeAppError, Seq[FinalParametrizedReport]]] =
    generateReport(projectQuery)

  private def generateReport(projectQuery: ReportBodyWithParamsRequest): F[Either[LogTimeAppError, Seq[FinalParametrizedReport]]] = {
    EitherT(getReport(projectQuery)).map(report => groupByOrdered(report)(project => (project.project_name, project.project_create_time)).map {
      case ((projectName, projectCreatedTime), reportList) => {

        val reportTasks = reportList.map(task =>
          ReportTask(
            task.task_create_time, task.task_description,
            task.start_time, task.end_time, task.duration,
            task.volume, task.comment))

        FinalParametrizedReport(projectName, projectCreatedTime, reportTasks.toList)
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