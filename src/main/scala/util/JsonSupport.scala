package util

import java.time.{LocalDateTime, ZonedDateTime}

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import models.model.{ByCreatedTime, ByUpdateTime, ProjectSort, ProjectTb, TaskTb, UserTb}
import models.request.{ChangeProjectNameRequest, CreateProjectRequest, DeleteProjectRequest, DeleteTaskRequest, LogTaskRequest, ReportRequest, UpdateTaskRequest}
import models.responses.{FinalReport, ProjectReport, Tasks}
import spray.json._

import scala.util.{Failure, Success, Try}


trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {

  implicit val localDateTimeFormat: JsonFormat[LocalDateTime] =
    new JsonFormat[LocalDateTime] {
      override def write(obj: LocalDateTime): JsValue = JsString(obj.toString)

      override def read(json: JsValue): LocalDateTime = json match {
        case JsString(s) => Try(LocalDateTime.parse(s)) match {
          case Success(result) => result
          case Failure(exception) =>
            deserializationError(s"could not parse $s as ZoneDateTime", exception)
        }
        case notAJsString =>
          deserializationError(s"expected a String but got a $notAJsString")
      }
    }

  implicit val zonedLocalDateTimeFormat: JsonFormat[ZonedDateTime] =
    new JsonFormat[ZonedDateTime] {
      override def write(obj: ZonedDateTime): JsValue = JsString(obj.toString)

      override def read(json: JsValue): ZonedDateTime = json match {
        case JsString(s) => Try(ZonedDateTime.parse(s)) match {
          case Success(result) => result
          case Failure(exception) =>
            deserializationError(s"could not parse $s as ZoneDateTime", exception)
        }
        case notAJsString =>
          deserializationError(s"expected a String but got a $notAJsString")
      }
    }




//  implicit val userFormat: RootJsonFormat[UserTb] = jsonFormat2(UserTb)
//  implicit val projectFormat: RootJsonFormat[ProjectTb] = jsonFormat6(ProjectTb)
//  implicit val projectDataFormat: RootJsonFormat[CreateProjectRequest] = jsonFormat2(CreateProjectRequest)
//  implicit val changeProjectNameFormat: RootJsonFormat[ChangeProjectNameRequest] = jsonFormat3(ChangeProjectNameRequest)
//  implicit val deleteProjectFormat: RootJsonFormat[DeleteProjectRequest] = jsonFormat2(DeleteProjectRequest)
//  implicit val logTaskFormat: RootJsonFormat[LogTaskRequest] = jsonFormat7(LogTaskRequest)
//  implicit val taskFormat: RootJsonFormat[TaskTb] = jsonFormat12(TaskTb)
//  implicit val deletedTaskFormat: RootJsonFormat[DeleteTaskRequest] = jsonFormat3(DeleteTaskRequest)
//  implicit val updateTaskFormat: RootJsonFormat[UpdateTaskRequest] = jsonFormat7(UpdateTaskRequest)
//  implicit val tasksFormat: RootJsonFormat[Tasks] = jsonFormat1(Tasks)
//  implicit val projectReportFormat: RootJsonFormat[ProjectReport] = jsonFormat3(ProjectReport)
//  implicit val finalReportFormat: RootJsonFormat[FinalReport] = jsonFormat10(FinalReport)
//  implicit val projectSortByCreatedTime = jsonFormat0(ByCreatedTime)
//  implicit val projectSortByUpdateTime = jsonFormat(ByUpdateTime)
//  implicit val reportReqFormat = jsonFormat8(ReportRequest)
}