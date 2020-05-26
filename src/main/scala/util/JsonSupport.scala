package util

import java.time.{LocalDateTime, ZonedDateTime}

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import data.Entities._
import data._
import spray.json._
import doobie.implicits.javatime._
import error.{AppError, ProjectNotCreated}

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




  implicit val userFormat: RootJsonFormat[User] = jsonFormat2(User)
  implicit val projectFormat: RootJsonFormat[Entities.Project] = jsonFormat6(Entities.Project)
  implicit val projectDataFormat: RootJsonFormat[CreateProject] = jsonFormat2(CreateProject)
  implicit val changeProjectNameFormat: RootJsonFormat[ChangeProjectName] = jsonFormat3(ChangeProjectName)
  implicit val deleteProjectFormat: RootJsonFormat[DeleteProject] = jsonFormat2(DeleteProject)
  implicit val logTaskFormat: RootJsonFormat[LogTaskModel] = jsonFormat7(LogTaskModel)
  implicit val taskFormat: RootJsonFormat[Task] = jsonFormat11(Task)
  implicit val deletedTaskFormat: RootJsonFormat[DeleteTask] = jsonFormat3(DeleteTask)
  implicit val updateTaskFormat: RootJsonFormat[UpdateTask] = jsonFormat7(UpdateTask)
  implicit val projecReportFormat: RootJsonFormat[ProjecReport] =jsonFormat1(ProjecReport)
  implicit val tasksFormat: RootJsonFormat[Tasks] = jsonFormat1(Tasks)
  implicit val projectReportFormat: RootJsonFormat[ProjectReport] = jsonFormat3(ProjectReport)
  implicit val finalReportFormat: RootJsonFormat[FinalReport] = jsonFormat9(FinalReport)
}