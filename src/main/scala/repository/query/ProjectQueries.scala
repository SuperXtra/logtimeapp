package repository.query

import java.time._
import models.model._
import doobie.implicits.javatime._
import models._
import slick.sql._
import slick.jdbc.PostgresProfile.api._
import db.ColumnImplicits._
import db.ProjectSchema


object ProjectQueries {
  val project = ProjectSchema.projects

  def insert(projectName: String, userId: UserId): FixedSqlAction[Project, NoStream, Effect.Write] = {
    (project returning project) +=
      Project(
        userId = userId,
        projectName = projectName,
        createTime = ZonedDateTime.now(ZoneOffset.UTC).toLocalDateTime,
        deleteTime = None,
        active = Some(Active(true))
      )
  }

  def changeName(oldName: String, newName: String, userId: UserId): FixedSqlAction[Int, NoStream, Effect.Write] = {
    val result = (for {
      p <- project if p.projectName === oldName && p.userId === userId
    } yield p.projectName).update(newName)

    result.statements.foreach(println)
    result
  }

  def deactivate(requestingUserId: UserId, projectName: String, deleteTime: LocalDateTime): FixedSqlAction[Int, NoStream, Effect.Write] = {
    project.filter(p => p.projectName === projectName && p.userId === requestingUserId)
      .map(x => (x.deleteTime, x.active))
      .update((Some(deleteTime), Some(Active(false))))
  }

  def getIdByProjectName(projectName: String) =
    project.filter(_.projectName === projectName).result.headOption

  def getActiveProjectByName(projectName: String) = {
    project.filter(p => p.projectName === projectName && p.active === Active(true)).result
  }


  def projectExists(projectName: String) =
    project.filter(p => p.projectName === projectName).exists.result

  def checkIfUserIsOwner(userId: UserId, projectName: String) =
    project
      .filter(p =>
        p.projectName === projectName &&
          p.userId === userId &&
          p.active === Active(true))
      .exists.result
}