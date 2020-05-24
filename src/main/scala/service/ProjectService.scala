package service

import data.{ChangeProjectName, CreateProject, DeleteProject, Queries}
import dbConnection.PostgresDb
import java.util.UUID

import akka.http.scaladsl.model.DateTime
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
    //TODO handle situation when there is no user with given uuid

    val x = for {
      userId <- Queries.User.getUserId(project.userIdentification).unique
      projectId <- Queries.Project.insert(project.projectName, userId).unique
    } yield projectId

    x.transact(con).attemptSomeSqlState {
      case sqlstate.class23.UNIQUE_VIOLATION => s"Project with name: [ ${project.projectName} ] already exists, please select different name"
    }
  }

  def updateProjectName(project: ChangeProjectName) = {
    //TODO handle situation when there is no user with given uuid
    val y = for {
      userId <- Queries.User.getUserId(project.userIdentification).unique
      updateResult <- Queries.Project.changeName(project.oldProjectName,project.projectName, userId).run
      project <- Queries.Project.getProject(project.projectName).unique
    } yield (project, updateResult)

    y.transact(con).attemptSomeSqlState {
      case x => s"returned error is: ${x.value}"
    }
  }

  def deleteProject(project: DeleteProject) = {

    //TODO handle situation when there is no user with given uuid
    val z = for {
      user <- Queries.User.getUserId(project.userIdentification).unique
      deleteResult <- Queries.Project.deleteProject(user.toInt, project.projectName).run
      project <- Queries.Project.getProject(project.projectName).unique
    } yield (project, deleteResult)

    z.transact(con).attemptSomeSqlState {
      case x => s"returned error is ${x.value}"
    }
  }


}
