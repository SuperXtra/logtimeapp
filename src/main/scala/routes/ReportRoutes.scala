package routes

import java.time.ZonedDateTime

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.directives.ParameterDirectives
import cats.effect.IO
import error.AppError
import models.request.{RReq, RRequest, ReportRequest}
import models.responses.{FinalReport, ProjectReport, RepTasks, Repoooort}
import io.circe.generic.auto._
import cats.implicits._
import cats.effect._
import ParameterDirectives.ParamMagnet
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import models.model.{Ascending, ByCreatedTime, ByUpdateTime, Descending, ProjectSort, SortDirection}

import scala.collection.immutable.SortedMap

object ReportRoutes {


  def projectTasksReport(req: String => IO[Either[AppError, ProjectReport]]): Route =
    path("project") {
      parameter("name") { name =>
        get {
          complete(
            req(name).map {
              case Right(report) => report.asRight
              case Left(value: AppError) => value.asLeft
            }.unsafeToFuture
          )
        }
      }
    }


  def mainReport(req: ReportRequest => IO[Either[AppError, List[FinalReport]]]): Route = {
    path("report") {
      parameters(
        "by".as[String].?,
        "sort".as[String].?,
        "active".as[Boolean].?,
        "page".as[Int],
        "quantity".as[Int]
      ).as((a,b,c,d,e) =>{

        extractQuery(a,b,c,d,e)
      }) { pathParams: RReq =>
        get {
          entity(as[RRequest]) { request: RRequest =>
            complete(
              req(ReportRequest(request, pathParams)).map {
                case Right(response: List[FinalReport]) => {

                  val x = response.groupBy(a => (a.project_name, a.project_create_time)).map {
                    case (tuple, value) => {

                      val tasks = value.map(x=>{
                        println(x.toString)
                        RepTasks(x.task_create_time,
                          x.task_description,
                          x.start_time,
                          x.end_time,
                          x.duration,
                          x.volume,
                          x.comment)
                      }

                      )

                      Repoooort(
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

  private def resolveSort(sort: String) ={
    sort match {
      case "create" => ByCreatedTime
      case "update" =>ByUpdateTime
      case _ => throw new Exception(s"invalid sort type: $sort")
    }
  }

  private def resolveDirection(direction: String) = direction match {
    case "asc" =>Ascending
    case "desc" =>Descending
    case _ => throw new Exception(s"invalid sort type: $direction")
  }

  private def extractQuery(
                            sortBy: Option[String],
                            direction: Option[String],
                            active: Option[Boolean],
                            page: Int,
                            quantity: Int
                          ): RReq = RReq(
  active,
  sortBy.map(x=> {
    resolveSort(x)
  }),
  direction.map(x=>resolveDirection(x)),
  page,
  quantity

  )
}
