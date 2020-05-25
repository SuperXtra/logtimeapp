package util

import java.time.ZonedDateTime

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import data.Entities.{Project, Task, User}
import data.{ChangeProjectName, CreateProject, DeleteProject, DeleteTask, LogTask, ProjecReport, UpdateTask}
import spray.json._
import data.JodaDateTimeString.JodaDateTimeDecoder
import data.JodaDateTimeString.JodaDateTimeEncoder
import org.joda.time.LocalDateTime
import doobie.implicits.javatime._

import scala.util.{Failure, Success, Try}


trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {

  implicit val jodaLocalDateTimeFormat: JsonFormat[ZonedDateTime] =
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
  implicit val projectFormat: RootJsonFormat[Project] = jsonFormat6(Project)
  implicit val projectDataFormat: RootJsonFormat[CreateProject] = jsonFormat2(CreateProject)
  implicit val changeProjectNameFormat: RootJsonFormat[ChangeProjectName] = jsonFormat3(ChangeProjectName)
  implicit val deleteProjectFormat: RootJsonFormat[DeleteProject] = jsonFormat2(DeleteProject)
  implicit val logTaskFormat: RootJsonFormat[LogTask] = jsonFormat7(LogTask)
  implicit val taskFormat: RootJsonFormat[Task] = jsonFormat11(Task)
  implicit val deletedTaskFormat: RootJsonFormat[DeleteTask] = jsonFormat3(DeleteTask)
  implicit val updateTaskFormat: RootJsonFormat[UpdateTask] = jsonFormat7(UpdateTask)
  implicit val projectReportFormat: RootJsonFormat[ProjecReport] =jsonFormat1(ProjecReport)



}