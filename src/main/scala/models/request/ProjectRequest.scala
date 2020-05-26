package models.request

case class CreateProjectRequest(projectName: String, userIdentification: String)
case class ChangeProjectNameRequest(oldProjectName: String, projectName: String, userIdentification: String)
case class DeleteProjectRequest(projectName: String, userIdentification: String)


