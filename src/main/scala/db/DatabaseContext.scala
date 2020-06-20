package db

import cats.effect._
import config.DatabaseConfig
import slick.jdbc.PostgresProfile.api._

object DatabaseContext {
//  def transactor(databaseConfig: DatabaseConfig)(implicit cs: ContextShift[IO]) =
//    Transactor.fromDriverManager[IO](
//      databaseConfig.driver,
//      databaseConfig.url,
//      databaseConfig.userName,
//      databaseConfig.password
//    )

  def transactor[A](databaseConfig: DatabaseConfig)
                        (implicit cs: ContextShift[IO]) = {
    Database.forURL(
      databaseConfig.url,
      databaseConfig.userName,
      databaseConfig.password,
      null,
      databaseConfig.driver
    )
  }


//  def executeDBIO[T](dBIO: DBIO[T])(implicit db: Database): IO[T] = {
//    implicit val cs: ContextShift[IO] = IO.contextShift(ExecutionContexts.synchronous)
//    IO.fromFuture(IO{
//      db.run(dBIO)
//    })
//  }
//  def executeDBIOAction[T](dBIO: DBIOAction[T,NoStream,Nothing])(implicit db: Database): IO[T] = {
//    implicit val cs: ContextShift[IO] = IO.contextShift(ExecutionContexts.synchronous)
//    IO.fromFuture(IO{
//      db.run(dBIO)
//    })
//  }


  //  def transact[A](action: DBIO[A])(db : Database): IO[A] = {
  //    implicit val cs: ContextShift[IO] = IO.contextShift(ExecutionContexts.synchronous)
  //
  //    db.run(action)
  //        IO.fromFuture(
  //          IO(
  //            db.run(
  //              action
  //            )
  //          )
  //        )
  //  }
}
