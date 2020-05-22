package service

import cats.effect.IO
import data.{ProjectData, Queries}
import dbConnection.PostgresDb
import doobie.util.ExecutionContexts

class ProjectService() {
  //TODO

  val con = PostgresDb.xa
  implicit val cs = IO.contextShift(ExecutionContexts.synchronous)

  def createNewProject(project: ProjectData) = {
    val x = Queries.Project.insert(project)
    x

  }

}
