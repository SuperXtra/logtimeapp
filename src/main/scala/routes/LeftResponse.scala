package routes

import akka.http.scaladsl.model._
import errorMessages._
import cats.implicits._

object LeftResponse {
  def apply(error: AppBusinessError) = error match {
    case ProjectUpdateUnsuccessful() => StatusCodes.ExpectationFailed -> AppErrorResponse("error.project.update.unsuccessful")
    case ProjectDeleteUnsuccessful() => StatusCodes.ExpectationFailed -> AppErrorResponse("error.project.delete.unsuccessful")
    case ProjectNameExists() => StatusCodes.Conflict -> AppErrorResponse("error.project.name.exists")
    case ProjectNotCreated() => StatusCodes.ExpectationFailed -> AppErrorResponse("error.project.not.created")
    case ProjectNotFound() => StatusCodes.NotFound -> AppErrorResponse("error.project.name.not.found")
    case ProjectDeleteUnsuccessfulUserIsNotTheOwner() => StatusCodes.NotFound -> AppErrorResponse("error.project.delete.unsuccesfull.not.owner")
    case TaskNotCreated() => StatusCodes.ExpectationFailed -> AppErrorResponse("error.task.not.created")
    case TaskNotFound() => StatusCodes.ExpectationFailed -> AppErrorResponse("error.task.not.found")
    case TaskNameExists() => StatusCodes.ExpectationFailed -> AppErrorResponse("error.task.name.exists")
    case TaskUpdateUnsuccessful() => StatusCodes.ExpectationFailed -> AppErrorResponse("error.task.update.unsuccessful")
    case TaskDeleteUnsuccessful() => StatusCodes.ExpectationFailed -> AppErrorResponse("error.task.delete.unsuccessful")
    case UserNotFound() => StatusCodes.ExpectationFailed -> AppErrorResponse("error.user.not.found")
    case CannotCreateUserWithGeneratedUUID() => StatusCodes.ExpectationFailed -> AppErrorResponse("error.user.cannot.create")
    case ReportCouldNotBeGenerated() => StatusCodes.ExpectationFailed -> AppErrorResponse("error.report.cannot.generate")
    case AuthenticationNotSuccessful() => StatusCodes.Unauthorized -> AppErrorResponse("error.authentication.not.successful")
    case AuthenticationNotSuccessfulWithoutBearer() => StatusCodes.Unauthorized -> AppErrorResponse("error.authentication.not.successful.missing.bearer.prefix")
  }

}