package routes

import java.time.ZonedDateTime

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.directives.ParameterDirectives
import cats.effect.IO
import errorMessages.AppBusinessError
import models.request.{MainReport, ReportBodyWithParamsRequest, ReportParams, ReportRequest}
import models.responses.{DetailReportResponse, GeneralReport, ReportFromDb, ReportTask, UserStatisticsReport}
import io.circe.generic.auto._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import cats.implicits._
import akka.http.scaladsl.model.{HttpEntity, StatusCodes}
import models.model.{Ascending, ByCreatedTime, ByUpdateTime, Descending, ProjectSort, SortDirection}
import models.responses
import service.auth.Auth

object ReportRoutes {

  def projectTasksReport(req: String => IO[Either[AppBusinessError, GeneralReport]])
                        (implicit auth: Auth): Route =
    path("project") {
      parameter("name") { name =>
        get {
          auth.apply { _ =>
            complete(
              req(name)
                .map(_.leftMap(LeftResponse(_)))
                .unsafeToFuture
            )
          }
        }
      }
    }

  def detailedReport(req: MainReport => IO[Either[AppBusinessError, List[UserStatisticsReport]]])
                    (implicit auth: Auth) =
    path("detailed") {
      get {
        auth.apply { _ =>
          entity(as[MainReport]) { request =>
            complete(
              req(request)
                .map(_.leftMap(LeftResponse(_)))
                .unsafeToFuture
            )
          }
        }
      }
    }


  def mainReport(req: ReportBodyWithParamsRequest => IO[Either[AppBusinessError, Seq[responses.DetailReportResponse]]])
                (implicit auth: Auth): Route = path("report") {
    parameters(
      "by".as[String].?,
      "sort".as[String].?,
      "active".as[Boolean].?,
      "page".as[Int],
      "quantity".as[Int]
    ).as(extractQuery) { pathParams: ReportParams =>
      get {
        auth.apply { _ =>
          entity(as[ReportRequest]) { request: ReportRequest =>
            complete(
              req(ReportBodyWithParamsRequest(request, pathParams))
                .map(_.leftMap(LeftResponse(_)))
                .unsafeToFuture
            )
          }
        }
      }
    }
  }

  private def extractQuery(
                            sortBy: Option[String],
                            direction: Option[String],
                            active: Option[Boolean],
                            page: Int,
                            quantity: Int
                          ): ReportParams = ReportParams(
    active,
    sortBy.map(resolveSort),
    direction.map(resolveDirection),
    if (page < 1) 1 else page,
    if (quantity <= 0) 20 else 20
  )

  private def resolveSort(sort: String) = {
    sort match {
      case "create" => ByCreatedTime
      case "update" => ByUpdateTime
      case _ => throw new Exception(s"invalid sort type: $sort")
    }
  }

  private def resolveDirection(direction: String) = direction match {
    case "asc" => Ascending
    case "desc" => Descending
    case _ => throw new Exception(s"invalid sort type: $direction")
  }


}
