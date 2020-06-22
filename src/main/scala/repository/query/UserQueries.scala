package repository.query

import models.model._
import models.UserId
import db.ColumnImplicits._
import db.UserSchema
import slick.jdbc.PostgresProfile.api._
import slick.sql.FixedSqlAction

object UserQueries {
  val users = UserSchema.users

  def insertUser(userIdentification: String): FixedSqlAction[UserId, NoStream, Effect.Write] = {
    users returning users.map(_.userId) +=
      User(userIdentification = userIdentification)
  }

  def getUserById(id: UserId) = {
    users.filter(u => u.userId === id).result.headOption
  }

  def getUserIdByUUID(userIdentification: String): DBIO[Option[User]] = {
    users.filter(u => u.userIdentification === userIdentification).result.headOption
  }

  def userExists(userIdentification: String): DBIO[Boolean] = {
    users.filter(u => u.userIdentification === userIdentification).exists.result
  }
}