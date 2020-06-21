package repository.query

import java.sql.Timestamp
import java.time._

import doobie.Update0
import doobie.implicits._
import doobie.util.query.Query0
import models.model._
import doobie.implicits.javatime._
import models.{Active, IsOwner, ProjectId, UserId}
import slick.jdbc.{GetResult, PositionedParameters, SetParameter}
import slick.sql._
import slick.jdbc.PostgresProfile.api._
import ColumnImplicits._


object ProjectQueries {
  val project = ProjectSchema.projects

  def insertSlick(projectName: String, userId: UserId): FixedSqlAction[Project, NoStream, Effect.Write] = {
    (project returning project) +=
      Project(
        userId = userId,
        projectName = projectName,
        createTime = ZonedDateTime.now(ZoneOffset.UTC).toLocalDateTime,
        deleteTime = None,
        active = Some(Active(true))
      )
  }

  def changeNameSlick(oldName: String, newName: String, userId: UserId): FixedSqlAction[Int, NoStream, Effect.Write] = {
    val result = (for {
      p <- project if p.projectName === oldName && p.userId === userId
    } yield p.projectName).update(newName)

    result.statements.foreach(println)
    result
  }

  def deactivateSlick(requestingUserId: UserId, projectName: String, deleteTime: LocalDateTime): FixedSqlAction[Int, NoStream, Effect.Write] = {
       project.filter(p => p.projectName === projectName && p.userId === requestingUserId)
      .map(x => (x.deleteTime, x.active))
      .update((Some(deleteTime), Some(Active(false))))
  }

  def getIdByProjectNameSlick(projectName: String) =
    project.filter(_.projectName === projectName).result.headOption

  def getActiveProjectByNameSlick(projectName: String)= {
    project.filter(p => p.projectName === projectName && p.active === Active(true)).result
  }


  def projectExistsSlick(projectName: String) =
    project.filter(p => p.projectName === projectName).exists.result

  def checkIfUserIsOwnerSlick(userId: UserId, projectName: String) =
    project
      .filter(p =>
        p.projectName === projectName &&
          p.userId === userId &&
          p.active === Active(true))
      .exists.result
}