package routes

import akka.http.scaladsl.server.Directives.{complete, get, path}
import cats.effect.IO
import error.AppError
import akka.http.scaladsl.server.Directives._
import models.responses.ProjectReport
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
}
