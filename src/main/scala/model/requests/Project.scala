package model.requests

case class Project(projectName: String)

case class Task(taskName: String, startedTime: String, duration: String, volume: Option[Int], comment: Option[String])

case class Filter(projectIdentificators: Option[List[String]], from: Option[String], to: Option[String], deleted: Option[Boolean], sortBy: List[Option[(String, String)]], pageSize: Int, pageNumber: Int)