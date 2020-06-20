package repository.query

import models.model.{User, UserSchema}
import models.UserId
import models.model.ColumnImplicits._
import slick.jdbc.PostgresProfile.api._
import slick.sql.FixedSqlAction

object UserQueries {
  val users = UserSchema.users

  def insertUserSlick(userIdentification: String): FixedSqlAction[UserId, NoStream, Effect.Write] = {
    users returning users.map(_.userId) +=
      User(userIdentification = userIdentification)
  }

  def getUserByIdSlick(id: UserId) = {
    users.filter(u => u.userId === id).result.headOption
  }

  def getUserIdByUUIDSlick(userIdentification: String): DBIO[Option[User]] = {
    users.filter(u => u.userIdentification === userIdentification).result.headOption
  }

  def userExistsSlick(userIdentification: String): DBIO[Boolean] = {
    users.filter(u => u.userIdentification === userIdentification).exists.result
  }
}
