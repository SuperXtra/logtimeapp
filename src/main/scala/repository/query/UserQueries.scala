package repository.query

import models.model.User
import cats.implicits._
import doobie.free.connection.ConnectionIO
import doobie.util.log.LogHandler
import doobie.implicits.javatime._
import doobie.{Fragment, Update0}
import doobie.implicits._
import doobie.util.query.Query0
import models.model._
import models.request._
import models.responses.ReportFromDb

object UserQueries {

  def insertUser(userIdentification: String) = {
    sql"insert into tb_user (user_identification) values (${userIdentification}) returning id".query[Int]
  }

  def selectLastInsertedUser() = {
    sql"select lastval()".query[Long]
  }

  def selectByUserIdentity(id: Int) = {
    sql"select * from tb_user where id = $id".query[User]
  }

  def getUserId(userIdentification: String) = {
    fr"""
            select id from tb_user
            where user_identification = ${userIdentification}
            """.query[Int]
  }

  def userExists(userIdentification: String) = {
    sql"select exists(select * from tb_user where user_identification = ${userIdentification})".query[Boolean]
  }

}
