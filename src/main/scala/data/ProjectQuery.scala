package data

import akka.http.scaladsl.model.DateTime
import doobie.{Fragment, Query0, Update0}
import doobie.implicits._

object ProjectQuery {

  def insert(project: ProjectData): Update0 = {
    val created = DateTime.now.toString()
    fr"""insert into project (user_id, project_name, created_time) VALUES (
        ${project.user},
        ${project.projectName}
        ${created}
        )""".update
  }

  def changeName(oldName: String, newName: String, requestingUserId: Int): Update0 = {
    fr"""
        update project set project_name = ${newName}
        where project_name = ${oldName}
        and user_id = ${requestingUserId}
        """.update
  }

  def deleteProject(requestingUserId: Int, projectName: String, deleted: String): Update0 = {
    fr"""
        update project set deleted_time = ${deleted}
        where project_name = ${projectName}
        and user_id = ${requestingUserId}
        """.update
  }

  def getProject(projectName: String) = {
    fr"select id, user_id, project_name, created_time from project where project_name = ${projectName}".query[ProjectEntity]
  }
}
