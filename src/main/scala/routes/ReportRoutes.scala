package routes

import akka.http.scaladsl.server.Directives.{complete, get, path}
import cats.effect.IO
import error.AppError
import akka.http.scaladsl.server.Directives._
import models.request.ReportRequest
import models.responses.{FinalReport, ProjectReport}
import util.JsonSupport

object ReportRoutes extends JsonSupport{



  def projectTasksReport(req: String => IO[Either[AppError, ProjectReport]]) =
    path("project" / Segment) { projectName: String =>
      get {
        req(projectName).map {
          case Left(value: AppError) => complete {
            println("error")
            value.toString
          }
          case Right(report) => complete{
            println("right")

            println(report.toString)
            report
          }
        }.unsafeRunSync()
      }
    }
//
//  def mainReport(req: ReportRequest => IO[Either[AppError, List[FinalReport]]]) = {
//    path("report")
//      get {
//        entity(as[ReportRequest]) { request =>
//        req(request).map {
//          case Left(value) => complete(value.toString)
//          case Right(value) => complete(value)
//        }.unsafeRunSync()
//      }
//    }
//  }
}
