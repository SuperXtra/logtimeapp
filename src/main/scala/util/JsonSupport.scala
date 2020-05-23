package util

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import data.Entities.{Project, User}
import data.{ChangeProjectName, CreateProject}
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val userFormat: RootJsonFormat[User] = jsonFormat2(User)
  implicit val projectFormat: RootJsonFormat[Project] = jsonFormat5(Project)
  implicit val projectDataFormat: RootJsonFormat[CreateProject] = jsonFormat2(CreateProject)
  implicit val changeProjectNameFormat: RootJsonFormat[ChangeProjectName] = jsonFormat3(ChangeProjectName)
}
