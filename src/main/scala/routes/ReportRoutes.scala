package routes

import java.time.ZonedDateTime

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.directives.ParameterDirectives
import cats.effect.IO
import errorMessages.{AppBusinessError, RouteErrorMsg}
import models.request.{MainReport, ReportBodyWithParamsRequest, ReportParams, ReportRequest}
import models.responses.{FinalParametrizedReport, FinalProjectReport, ReportFromDb, ReportTask, OverallStatisticsReport}
import io.circe.generic.auto._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import cats.implicits._
import akka.http.scaladsl.model.{HttpEntity, StatusCodes}
import io.circe.Printer
import models.model.{Ascending, ByCreatedTime, ByUpdateTime, Descending, ProjectSort, SortDirection}
import models.responses
import service.auth.Auth

object ReportRoutes {

  val printer = Printer.noSpaces.copy(dropNullValues = true)

  def projectTasksReport(req: String => IO[Either[AppBusinessError, FinalProjectReport]])
                        (implicit auth: Auth): Route =
    pathPrefix("report" / "project") {
      parameter("name") { projectName =>
        get {
          auth.apply { _ =>
            complete(
              req(projectName)
                .map(_.leftMap(RouteErrorMsg.report))
                .unsafeToFuture
            )
          }
        }
      }
    }

  def detailedReport(req: MainReport => IO[Either[AppBusinessError, OverallStatisticsReport]])
                    (implicit auth: Auth) =
    pathPrefix("report" / "users") {
      get {
        auth.apply { _ =>
          entity(as[MainReport]) { request =>
            complete(
              req(request)
                .map(_.leftMap(RouteErrorMsg.report))
                .unsafeToFuture
            )
          }
        }
      }
    }


  def mainReport(req: ReportBodyWithParamsRequest => IO[Either[AppBusinessError, Seq[responses.FinalParametrizedReport]]])
                (implicit auth: Auth): Route =
    pathPrefix("report" / "detail") {
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
                .map(_.leftMap(RouteErrorMsg.report))
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
    if (quantity <= 0) 20 else quantity
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
