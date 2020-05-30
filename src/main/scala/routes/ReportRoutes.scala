package routes

import java.time.ZonedDateTime

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.directives.ParameterDirectives
import cats.effect.IO
import error.AppError
import models.request.{MainReport, ReportBodyWithParamsRequest, ReportParams, ReportRequest}
import models.responses.{DetailReport, GeneralReport, ReportFromDb, ReportTask, UserStatisticsReport}
import io.circe.generic.auto._
import cats.implicits._
import cats.effect._
import ParameterDirectives.ParamMagnet
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import models.model.{Ascending, ByCreatedTime, ByUpdateTime, Descending, ProjectSort, SortDirection}
import routes.ProjectRoutes.Authorization
import service.auth.Authenticate

import scala.collection.immutable.SortedMap

object ReportRoutes {

  val authorization = new Authenticate()


  def projectTasksReport(req: String => IO[Either[AppError, GeneralReport]]): Route =
    path("project") {
      parameter("name") { name =>
        get {
          Authorization.authenticated { _ =>
            complete(
              req(name).map {
                case Right(report) => report.asRight
                case Left(value: AppError) => value.asLeft
              }.unsafeToFuture
            )
          }
        }
      }
    }

  def detailedReport(req: MainReport => IO[Either[AppError, List[UserStatisticsReport]]]) =
    path("detailed") {
      get{
        Authorization.authenticated { _ =>
          entity(as[MainReport]) { request =>
            complete(
              req(request).map {
                case Left(err) => err.asLeft
                case Right(report) => report.asRight
              }.unsafeToFuture
            )
          }
        }
      }
    }


  def mainReport(req: ReportBodyWithParamsRequest => IO[Either[AppError, List[ReportFromDb]]]): Route = {
    path("report") {
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
          Authorization.authenticated { _ =>
            entity(as[ReportRequest]) { request: ReportRequest =>
              complete(
                req(ReportBodyWithParamsRequest(request, pathParams)).map {
                  case Right(response: List[ReportFromDb]) => {

                    //TODO preserve order in groupBy
                    val x = response.groupBy(a => (a.project_name, a.project_create_time)).map {
                      case (tuple, value) => {

                        val tasks = value.map(x => {
                          println(x.toString)
                          ReportTask(x.task_create_time,
                            x.task_description,
                            x.start_time,
                            x.end_time,
                            x.duration,
                            x.volume,
                            x.comment)
                        }

                        )

                        DetailReport(
                          tuple._1,
                          tuple._2,
                          tasks
                        )
                      }
                    }
                    x.asRight
                  }
                  case Left(err: AppError) => err.asLeft
                }.unsafeToFuture
              )
            }
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
    page,
    quantity

  )
}
