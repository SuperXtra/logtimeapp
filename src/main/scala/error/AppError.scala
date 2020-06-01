package error

sealed trait AppError

//Project
case class ProjectUpdateUnsuccessful() extends AppError
case class ProjectDeleteUnsuccessful() extends AppError
case class ProjectNameExists() extends AppError
case class ProjectNotCreated() extends AppError
case class ProjectNotFound() extends AppError

//Task
case class TaskNotCreated() extends AppError
case class TaskNotFound() extends AppError
case class TaskNameExists() extends AppError
case class TaskUpdateUnsuccessful() extends AppError
case class TaskDeleteUnsuccessful() extends AppError


//User
case class UserNotFound() extends AppError
case class CannotCreateUserWithGeneratedUUID() extends AppError

//Report
case class ReportCouldNotBeGenerated() extends AppError

//Authentication
case class AuthenticationNotSuccessful() extends AppError