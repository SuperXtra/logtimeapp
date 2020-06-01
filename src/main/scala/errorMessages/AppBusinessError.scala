package errorMessages

sealed trait AppBusinessError

//Project
case class ProjectUpdateUnsuccessful() extends AppBusinessError
case class ProjectDeleteUnsuccessful() extends AppBusinessError
case class ProjectNameExists() extends AppBusinessError
case class ProjectNotCreated() extends AppBusinessError
case class ProjectNotFound() extends AppBusinessError
case class ProjectDeleteUnsuccessfulUserIsNotTheOwner() extends AppBusinessError

//Task
case class TaskNotCreated() extends AppBusinessError
case class TaskNotFound() extends AppBusinessError
case class TaskNameExists() extends AppBusinessError
case class TaskUpdateUnsuccessful() extends AppBusinessError
case class TaskDeleteUnsuccessful() extends AppBusinessError


//User
case class UserNotFound() extends AppBusinessError
case class CannotCreateUserWithGeneratedUUID() extends AppBusinessError

//Report
case class ReportCouldNotBeGenerated() extends AppBusinessError

//Authentication
case class AuthenticationNotSuccessful() extends AppBusinessError