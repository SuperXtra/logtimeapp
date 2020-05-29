package models.request

case class CreateProjectRequest(projectName: String)
case class ChangeProjectNameRequest(oldProjectName: String, projectName: String)
case class DeleteProjectRequest(projectName: String)


