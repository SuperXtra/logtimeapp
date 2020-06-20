package service.report

import slick.jdbc.PostgresProfile.api._
import akka.event.MarkerLoggingAdapter
import cats.effect.{ContextShift, IO, Sync}
import error._
import models.request.ReportBodyWithParamsRequest
import models.reports._
import repository.report.GetReport
import utils.EitherT

import scala.concurrent._
import ExecutionContext.Implicits.global
import db.RunDBIOAction._
import models.{TaskDuration, Volume}

class GetParametrizedReport[F[+_] : Sync](getReport: GetReport[F])
                                         (implicit tx: Database,
                                          logger: MarkerLoggingAdapter,
                                          ec: ContextShift[IO]) {

  def apply(projectQuery: ReportBodyWithParamsRequest): IO[Either[LogTimeAppError, Seq[FinalParametrizedReport]]] =
    generateReport(projectQuery)

  private def generateReport(projectQuery: ReportBodyWithParamsRequest) =
    EitherT(getReport(projectQuery)).map(report => groupByOrdered(report)(project => (project.project_name, project.project_create_time)).map {
      case ((projectName, projectCreatedTime), reportList) =>
        val reportTasks = reportList.map(task =>
          ReportTask(
            task.task_create_time, task.task_description,
            task.start_time, task.end_time, task.duration.map(TaskDuration),
            task.volume.map(Volume), task.comment))

        FinalParametrizedReport(projectName, projectCreatedTime, reportTasks.toList)
    }).value.exec

  private def groupByOrdered[A, K](xs: collection.Seq[A])(f: A => K): collection.Seq[(K, collection.Seq[A])] = {
    val m = collection.mutable.LinkedHashMap.empty[K, collection.Seq[A]].withDefault(_ => new collection.mutable.ArrayBuffer[A])
    xs.foreach { x =>
      val k = f(x)
      m(k) = m(k) :+ x
    }
    m.toSeq
  }
}