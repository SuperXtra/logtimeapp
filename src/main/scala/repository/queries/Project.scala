package repository.queries

import java.time.{ZoneOffset, ZonedDateTime}

import cats.implicits._
import doobie.free.connection.ConnectionIO
import doobie.util.log.LogHandler
import doobie.implicits.javatime._
import doobie.{Fragment, Update0}
import doobie.implicits._
import doobie.util.query.Query0
import models.model._
import models.request._
import models.responses.FinalReport

object Project {
  implicit val han = LogHandler.jdkLogHandler


  def insert(projectName: String, userId: Long) = {
    fr"""INSERT INTO tb_project (user_id, project_name, create_time) VALUES (
           ${userId},
           ${projectName},
           ${ZonedDateTime.now(ZoneOffset.UTC).toLocalDateTime}
           ) returning id
           """
      .query[Long]
      .unique
  }

  def changeName(oldName: String, newName: String, userId: Long): Update0 = {
    fr"""
        UPDATE tb_project SET project_name = ${newName}
        WHERE project_name = ${oldName}
        AND user_id = ${userId}
        """.update
  }

  def deleteProject(requestingUserId: Long, projectName: String, deleteTime: ZonedDateTime): Update0 = {
    fr"""UPDATE tb_project
           SET delete_time = ${deleteTime.toLocalDateTime}, active = false
           WHERE project_name = $projectName
           AND user_id = $requestingUserId
           """
      .update
  }

  def getProjectId(projectName: String): Query0[Long] =
    fr"""SELECT id FROM tb_project
           WHERE project_name = ${projectName}
           """
      .query[Long]

  def getProject(projectName: String): ConnectionIO[Option[ProjectTb]] = {
    fr"""SELECT * FROM tb_project
           WHERE project_name = ${projectName}"""
      .query[ProjectTb]
      .option
  }

  def projectExists(projectName: String): Query0[Boolean] = {
    fr"""SELECT EXISTS (
           SELECT * FROM tb_project
           WHERE project_name = ${projectName})
           """
      .query[Boolean]
  }

}
