package service

import data.Queries
import dbConnection.PostgresDb
import java.util.UUID
import doobie._
import doobie.implicits._
import doobie.util.ExecutionContexts
import cats.effect._

import cats.effect.IO
import data.{Entities, _}
import cats.free.Free
import cats.implicits._
import cats.syntax.comonad._
import cats.instances.list._
import data.Entities.User
import dbConnection.PostgresDb.xa
import doobie.free.connection
import doobie.util.ExecutionContexts


class UserService() {

//TODO: not working

  val con = PostgresDb.xa
  implicit val cs = IO.contextShift(ExecutionContexts.synchronous)

  def createNewUser(): IO[Either[Throwable, User]] = {
   val conn =  for {
      _ <-     Queries.User.insertUser(UUID.randomUUID().toString).run
      id <-     Queries.User.selectLastInsertedUser().unique
      user <-     Queries.User.selectByUserIdentity(id.toInt).unique
    } yield user
    conn.transact(con).attempt
  }

  def checkIfExists(uuid: String): IO[Either[Throwable, List[User]]] = {
    Queries.User.getUserId(uuid).to[List].transact(con).attempt
  }
}
