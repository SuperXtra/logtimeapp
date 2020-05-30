package routes

import java.time.ZonedDateTime

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.directives.ParameterDirectives
import cats.effect.IO
import error.AppError
import models.request.{MainReport, ReportBodyWithParamsRequest, ReportParams, ReportRequest}
import models.responses.{DetailReportResponse, GeneralReport, ReportFromDb, ReportTask, UserStatisticsReport}
import io.circe.generic.auto._
import cats.implicits._
import cats.effect._
import ParameterDirectives.ParamMagnet
import akka.http.scaladsl.model.StatusCodes
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import models.model.{Ascending, ByCreatedTime, ByUpdateTime, Descending, ProjectSort, SortDirection}
import models.responses
import service.auth.Authenticate

object ReportRoutes {

  val authorization = new Authenticate()


  def projectTasksReport(req: String => IO[Either[AppError, GeneralReport]]): Route =
    path("project") {
      parameter("name") { name =>
        get {
          authorization.authenticated { _ =>
            complete(
              req(name).map {
                case Right(report) => StatusCodes.OK -> report.asRight
                case Left(value: AppError) => StatusCodes.ExpectationFailed -> value.asLeft
              }.unsafeToFuture
            )
          }
        }
      }
    }

  def detailedReport(req: MainReport => IO[Either[AppError, List[UserStatisticsReport]]]) =
    path("detailed") {
      get {
        authorization.authenticated { _ =>
          entity(as[MainReport]) { request =>
            complete(
              req(request).map {
                case Right(report) => StatusCodes.OK -> report.asRight
                case Left(err) => StatusCodes.ExpectationFailed -> err.asLeft
              }.unsafeToFuture
            )
          }
        }
      }
    }


  def mainReport(req: ReportBodyWithParamsRequest => IO[Either[AppError, Seq[responses.DetailReportResponse]]]): Route = path("report") {
    parameters(
      "by".as[String].?,
      "sort".as[String].?,
      "active".as[Boolean].?,
      "page".as[Int],
      "quantity".as[Int]
    ).as((a, b, c, d, e) => {

      extractQuery(a, b, c, d, e)
    }) { pathParams: ReportParams =>
      get {
        authorization.authenticated { _ =>
          entity(as[ReportRequest]) { request: ReportRequest =>
            complete(
              req(ReportBodyWithParamsRequest(request, pathParams)).map {
                case Right(response) => StatusCodes.OK -> response.asRight
                case Left(err: AppError) => StatusCodes.ExpectationFailed -> err.asLeft
              }.unsafeToFuture
            )
          }
        }
      }
    }
  }


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

  private def extractQuery(
                            sortBy: Option[String],
                            direction: Option[String],
                            active: Option[Boolean],
                            page: Int,
                            quantity: Int
                          ): ReportParams = ReportParams(
    active,
    sortBy.map(x => {
      resolveSort(x)
    }),
    direction.map(x => resolveDirection(x)),
    if (page < 1) 1 else page,
    if (quantity <= 0) 20 else 20
  )

  def groupByOrdered[A, K](xs: collection.Seq[A])(f: A => K): collection.Seq[(K, collection.Seq[A])] = {
    val m = collection.mutable.LinkedHashMap.empty[K, collection.Seq[A]].withDefault(_ => new collection.mutable.ArrayBuffer[A])
    xs.foreach { x =>
      val k = f(x)
      m(k) = m(k) :+ x
    }
    m.toSeq
  }
}
