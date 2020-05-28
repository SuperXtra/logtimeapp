package repository.queries

import models.model.UserTb
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

object User {
  implicit val han = LogHandler.jdkLogHandler

  def insertUser(userIdentification: String) = {
    sql"insert into tb_user (user_identification) values (${userIdentification}) returning id".query[Int]
  }

  def selectLastInsertedUser() = {
    sql"select lastval()".query[Long]
  }

  def selectByUserIdentity(id: Int) = {
    sql"select * from tb_user where id = $id".query[UserTb]
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
