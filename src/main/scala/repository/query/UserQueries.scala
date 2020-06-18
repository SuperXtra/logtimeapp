package repository.query

import models.model.User
import doobie.implicits.javatime._
import doobie.implicits._
import models.UserId

object UserQueries {

  def insertUser(userIdentification: String) = {
    sql"INSERT INTO tb_user (user_identification) VALUES (${userIdentification}) RETURNING id".query[Int]
  }

  def getUserById(id: UserId) = {
    sql"SELECT * FROM tb_user WHERE id = ${id.value}".query[User]
  }

  def getUserIdByUUID(userIdentification: String) = {
    fr"""
            SELECT id FROM tb_user
            WHERE user_identification = ${userIdentification}
            """.query[Int]
  }

  def userExists(userIdentification: String) = {
    sql"SELECT EXISTS ( SELECT * FROM tb_user WHERE user_identification = ${userIdentification} )"
      .query[Boolean]
  }
}
