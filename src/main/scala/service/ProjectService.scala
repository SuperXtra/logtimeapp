package service

import data.{ChangeProjectName, CreateProject, Queries}
import dbConnection.PostgresDb
import java.util.UUID

import doobie.postgres._
import doobie.implicits._
import cats.effect.IO
import data.Entities.User
import doobie.util.ExecutionContexts
import doobie.util.log.LogHandler

class ProjectService() {
  //TODO

//  implicit val han = LogHandler.jdkLogHandler

  val con = PostgresDb.xa
  implicit val cs = IO.contextShift(ExecutionContexts.synchronous)

  def createNewProject(project: CreateProject) = {
    implicit val han = LogHandler.jdkLogHandler
    val x = for {
      userId <- Queries.User.getUserId(project.userIdentification).unique
      _ <- Queries.Project.insert(project.projectName, userId.id).run
      project <- Queries.Project.getProject(project.projectName).unique
    } yield project

    x.transact(con).attemptSomeSqlState {
      case sqlstate.class23.UNIQUE_VIOLATION => s"Project with name: [ ${project.projectName} ] already exists, please select different name"
    }
  }

  def updateProjectName(project: ChangeProjectName) = {
    val y = for {
      userId <- Queries.User.getUserId(project.userIdentification).unique
      updateResult <- Queries.Project.changeName(project.oldProjectName,project.projectName, userId.id).run
      project <- Queries.Project.getProject(project.projectName).unique
    } yield (project, updateResult)

    y.transact(con).attemptSomeSqlState {
      case x => s"returned error is: ${x.value}"
    }

  }
}
