package error

import akka.http.scaladsl.model.StatusCodes


object MapToErrorResponse {

  def project(error: LogTimeAppError) = error match {
    case ProjectNotCreated => StatusCodes.OK -> ErrorResponse("error.project.not.created.given.name.exists")
    case ProjectUpdateUnsuccessful => StatusCodes.OK -> ErrorResponse("error.project.update.unsuccessful.no.content.updated")
    case UserNotFound => StatusCodes.OK -> ErrorResponse("error.project.update.unsuccessful.user.not.found")
    case ProjectNameExists => StatusCodes.OK -> ErrorResponse("error.project.update.unsuccessful.name.exists")
    case ProjectDeleteUnsuccessful => StatusCodes.OK -> ErrorResponse("error.project.deactivation.unsuccessful")
    case ProjectNotFound => StatusCodes.OK -> ErrorResponse("error.project.could.not.find.project.with.given.name")
    case ProjectDeleteUnsuccessfulUserIsNotTheOwner => StatusCodes.OK -> ErrorResponse("error.project.delete.unsuccessful.user.not.owner")
    case TaskUpdateUnsuccessful => StatusCodes.OK -> ErrorResponse("error.project.update.unsuccessful.task.not.updated.successfully")
    case TaskNotFound => StatusCodes.OK -> ErrorResponse("error.project.task.not.found")
    case _ => StatusCodes.OK -> ErrorResponse("error.project.not.resolved")
  }

  def task(error: LogTimeAppError) = error match {
    case UserNotFound => StatusCodes.OK -> ErrorResponse("error.task.user.not.found")
    case TaskNotCreated => StatusCodes.OK -> ErrorResponse("error.task.not.created")
    case TaskNotFound => StatusCodes.OK -> ErrorResponse("error.task.not.found")
    case ProjectNotFound => StatusCodes.OK -> ErrorResponse("error.task.not.created.project.not.found")
    case TaskNameExists => StatusCodes.OK -> ErrorResponse("error.task.name.exists")
    case TaskNotCreatedExclusionViolation => StatusCodes.OK -> ErrorResponse("error.task.not.created.overlapping.time")
    case TaskUpdateUnsuccessful => StatusCodes.OK -> ErrorResponse("error.task.not.updated")
    case _ => StatusCodes.OK -> ErrorResponse("error.task.not.resolved")
  }

  def report(error: LogTimeAppError) = error match {
    case ReportCouldNotBeGenerated => StatusCodes.OK -> ErrorResponse("error.report.not.generated")
    case ProjectNotFound => StatusCodes.OK -> ErrorResponse("error.report.project.not.found")
    case TaskNotFound => StatusCodes.OK -> ErrorResponse("error.report.task.not.found")
    case _ => StatusCodes.OK -> ErrorResponse("error.report.not.generated")
  }

  def auth(error: LogTimeAppError) = error match {
    case AuthenticationNotSuccessful => StatusCodes.Unauthorized -> ErrorResponse("error.authentication.not.successful")
    case AuthenticationNotSuccessfulWithoutBearer => StatusCodes.Unauthorized -> ErrorResponse("error.authentication.not.successful.missing.bearer.prefix")
    case _ => StatusCodes.OK -> ErrorResponse("error.auth.not.defined")
  }

  def user(error: LogTimeAppError) = error match {
    case CannotCreateUserWithGeneratedUUID => StatusCodes.OK -> ErrorResponse("error.user.not.created.duplicate.uuid")
    case UserNotFound => StatusCodes.OK -> ErrorResponse("error.user.not.found")
    case _ => StatusCodes.OK -> ErrorResponse("not.defined.error")
  }
}