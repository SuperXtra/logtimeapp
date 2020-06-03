package routes

import akka.http.scaladsl.model._
import errorMessages._
import cats.implicits._

object LeftResponse {

  def project(error: AppBusinessError) = error match {
    case ProjectNotCreated() => StatusCodes.OK -> AppErrorResponse("error.project.update.unsuccessful.given.name.exists")
    case ProjectUpdateUnsuccessful() => StatusCodes.OK -> AppErrorResponse("error.project.update.unsuccessful.no.content.updated")
    case UserNotFound() => StatusCodes.OK -> AppErrorResponse("error.project.update.unsuccessful.user.not.found")
    case ProjectNameExists() => StatusCodes.OK -> AppErrorResponse("error.project.update.unsuccessful.name.exists")
    case ProjectDeleteUnsuccessful() => StatusCodes.OK -> AppErrorResponse("error.project.update.unsuccessful.name.exists")
    case ProjectNotFound() => StatusCodes.OK -> AppErrorResponse("error.project.could.not.find.project.with.given.name")
    case ProjectDeleteUnsuccessfulUserIsNotTheOwner() => StatusCodes.OK -> AppErrorResponse("error.project.delete.unsuccessful.user.not.owner")
    case TaskUpdateUnsuccessful() => StatusCodes.OK -> AppErrorResponse("error.project.update.unsuccessful.task.not.updated.successfully")
    case _ => StatusCodes.OK -> AppErrorResponse("error.project.not.resolved")
  }

  def task(error: AppBusinessError) = error match {
    case UserNotFound() => StatusCodes.OK -> AppErrorResponse("error.task.user.not.found")
    case TaskNotCreated() => StatusCodes.OK -> AppErrorResponse("error.task.not.created")
    case ProjectNotFound() => StatusCodes.OK -> AppErrorResponse("error.task.not.created.project.not.found")
    case TaskNameExists() => StatusCodes.OK -> AppErrorResponse("error.task.name.exists")
    case TaskNotCreatedExclusionViolation() => StatusCodes.OK -> AppErrorResponse("error.task.not.created.overlapping.time")
    case TaskUpdateUnsuccessful() => StatusCodes.OK -> AppErrorResponse("error.task.not.updated")
    case _ => StatusCodes.OK -> AppErrorResponse("error.task.not.resolved")
  }

  def report(error: AppBusinessError) = error match {
    case ReportCouldNotBeGenerated() => StatusCodes.OK -> AppErrorResponse("error.report.not.generated")
    case ProjectNotFound() => StatusCodes.OK -> AppErrorResponse("error.report.project.not.found")
    case TaskNotFound() => StatusCodes.OK -> AppErrorResponse("error.report.task.not.found")
    case _ => StatusCodes.OK -> AppErrorResponse("error.report.not.generated")
  }

  def auth(error: AppBusinessError) = error match {
    case AuthenticationNotSuccessful() => StatusCodes.Unauthorized -> AppErrorResponse("error.authentication.not.successful")
    case AuthenticationNotSuccessfulWithoutBearer() => StatusCodes.Unauthorized -> AppErrorResponse("error.authentication.not.successful.missing.bearer.prefix")
    case _ => StatusCodes.OK -> AppErrorResponse("error.auth.not.defined")
  }


  def user(error: AppBusinessError) = error match {
    case CannotCreateUserWithGeneratedUUID() => StatusCodes.OK -> AppErrorResponse("error.user.not.created.duplicate.uuid")
    case UserNotFound() => StatusCodes.OK-> AppErrorResponse("error.user.not.found")
    case _ => StatusCodes.OK -> AppErrorResponse("not.defined.error")
  }
}