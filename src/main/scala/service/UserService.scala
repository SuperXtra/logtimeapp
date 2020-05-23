package service

import data.Queries
import dbConnection.PostgresDb
import java.util.UUID
import doobie.implicits._
import cats.effect.IO
import data.Entities.User
import doobie.util.ExecutionContexts


class UserService() {

  val con = PostgresDb.xa
  implicit val cs = IO.contextShift(ExecutionContexts.synchronous)

  def createNewUser() = {
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
