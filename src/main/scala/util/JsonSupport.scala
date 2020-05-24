package util

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.DateTime
import data.Entities.{Project, Task, User}
import data.{ChangeProjectName, CreateProject, DeleteProject, DeleteTask, LogTask, ProjecReport, UpdateTask}
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val userFormat: RootJsonFormat[User] = jsonFormat2(User)
  implicit val projectFormat: RootJsonFormat[Project] = jsonFormat6(Project)
  implicit val projectDataFormat: RootJsonFormat[CreateProject] = jsonFormat2(CreateProject)
  implicit val changeProjectNameFormat: RootJsonFormat[ChangeProjectName] = jsonFormat3(ChangeProjectName)
  implicit val deleteProjectFormat: RootJsonFormat[DeleteProject] = jsonFormat2(DeleteProject)
  implicit val logTaskFormat: RootJsonFormat[LogTask] = jsonFormat7(LogTask)
  implicit val taskFormat: RootJsonFormat[Task] = jsonFormat10(Task)
  implicit val deletedTaskFormat: RootJsonFormat[DeleteTask] = jsonFormat3(DeleteTask)
  implicit val updateTaskFormat: RootJsonFormat[UpdateTask] = jsonFormat7(UpdateTask)
  implicit val projectReportFormat: RootJsonFormat[ProjecReport] =jsonFormat1(ProjecReport)
}