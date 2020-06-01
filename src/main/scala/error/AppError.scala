package error

sealed trait AppError

//Project
case object ProjectUpdateUnsuccessful extends AppError
case object ProjectDeleteUnsuccessful extends AppError
case object ProjectNameExists extends AppError
case object ProjectNotCreated extends AppError
case object ProjectNotFound extends AppError

//Task
case class TaskNotCreated(errorMessage: String = "Could not create new task", detailErrorMessage:String ="") extends AppError
case class TaskNotFound(errorMessage: String = "Could not find task with given parameters", detailErrorMessage:String ="") extends AppError
case class TaskNameExists(errorMessage: String = "Could not create task, task with given name exists already", detailErrorMessage:String ="") extends AppError
case class TaskUpdateUnsuccessful(errorMessage: String = "Could not update task", detailErrorMessage:String ="") extends AppError
case class TaskDeleteUnsuccessful(errorMessage: String = "Could not delete task", detailErrorMessage:String ="") extends AppError


//User
case class UserNotFound(errorMessage: String = "Could not find user", detailErrorMessage:String ="") extends AppError
case class CannotCreateUserWithGeneratedUUID(errorMessage: String = "Test Test") extends AppError

//Report
case class ReportCouldNotBeGenerated(errorMessage: String = "Report could not be generated", detailErrorMessage:String ="") extends AppError

//Authentication
case class AuthenticationNotSuccessful(errorMessage: String = "Could not authenticate request", detailErrorMessage:String ="") extends AppError