package repository.query

import java.time._

import doobie.Update0
import doobie.implicits._
import doobie.util.query.Query0
import models.model._
import doobie.implicits.javatime._
import doobie.util.log.LogHandler
import models.UserId

object ProjectQueries {

  def insert(projectName: String, userId: UserId): doobie.Query0[Int] = {
    fr"""INSERT INTO tb_project (user_id, project_name, create_time) VALUES (
           ${userId.value},
           ${projectName},
           ${ZonedDateTime.now(ZoneOffset.UTC).toLocalDateTime}
           ) returning id
           """
      .query[Int]
  }
  def changeName(oldName: String, newName: String, userId: UserId): Update0 = {
    fr"""
        UPDATE tb_project SET project_name = ${newName}
        WHERE project_name = ${oldName}
        AND user_id = ${userId.value}
        """.update
  }

  def deactivate(requestingUserId: UserId, projectName: String, deleteTime: LocalDateTime): Update0 = {
    fr"""UPDATE tb_project
           SET delete_time = ${deleteTime}, active = false
           WHERE project_name = $projectName
           AND user_id = ${requestingUserId.value}
           """
      .update
  }

  def getIdByProjectName(projectName: String): Query0[Long] =
    fr"""SELECT id FROM tb_project
           WHERE project_name = ${projectName}
           """
      .query[Long]

  def getActiveProjectByName(projectName: String)= {
    fr"""SELECT * FROM tb_project
           WHERE project_name = ${projectName}
           AND active = true"""
      .query[Project]
  }


  def projectExists(projectName: String): Query0[Boolean] = {
    fr"""SELECT EXISTS (
           SELECT * FROM tb_project
           WHERE project_name = ${projectName})
           """
      .query[Boolean]
  }

  def checkIfUserIsOwner(userId: UserId, projectName: String): doobie.Query0[Boolean] = {
    fr"""
        SELECT EXISTS ( select (id) FROM tb_project WHERE project_name = ${projectName} AND user_id = ${userId.value} and active = true)
        """.query[Boolean]
  }
}
