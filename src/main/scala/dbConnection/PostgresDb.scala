package dbConnection

import java.util.UUID

import doobie._
import doobie.implicits._
import doobie.util.ExecutionContexts
import cats.effect._
import cats.implicits._
import com.typesafe.config.ConfigFactory
import config.DatabaseConfig
import data.Entities.User
import pureconfig._
import pureconfig.generic.auto._

object PostgresDb {

//  val config = ConfigFactory.load("database-config.conf")
//  val databaseConfig = ConfigSource.fromConfig(config).loadOrThrow[DatabaseConfig]

  implicit val cs = IO.contextShift(ExecutionContexts.synchronous)

//  val xa = Transactor.fromDriverManager[IO](
//    databaseConfig.driver,
//    databaseConfig.url,
//    databaseConfig.userName,
//    databaseConfig.password
//  )

  val xa = Transactor.fromDriverManager[IO](
    "org.postgresql.Driver",
    "jdbc:postgresql://ec2-54-75-229-28.eu-west-1.compute.amazonaws.com:5432/d94gigncif0u25",
    "vqidaoxnepgktr",
    "5075aa01fcdf2e9f371b817a92621d5da74acc7a655f3dd29585445f5fd4ffde"
//    Blocker.liftExecutionContext(ExecutionContexts.synchronous)
  )

//
  val createUser =
    sql"""
         create table if not exists tb_user (
            id serial primary key,
            user_identification varchar(50) unique not null
          )
         """.update.run
//
//  val createProject =
//    sql"""
//         create table if not exists tb_project (
//            id serial primary key,
//            user_id integer not null,
//            project_name varchar (255) unique not null,
//            create_time varchar (255) not null,
//            delete_time varchar (255) default null,
//            foreign key (user_id) references tb_user (id)
//            )
//         """.update.run
//
//  val createTask =
//    sql"""
//         create table if not exists tb_task (
//            id serial primary key,
//            project_id integer not null,
//            user_id integer not null,
//            task_description varchar (255) not null,
//            start_time varchar (255) not null,
//            end_time varchar (255) not null,
//            volume integer,
//            comment varchar (255),
//            delete_time varchar(255) default null,
//            foreign key (project_id) references tb_project (id),
//            foreign key (user_id) references tb_user (id),
//            constraint uq_project_task_desc unique (project_id, task_description)
//         )
//         """.update.run
//
//  (createUser, createProject, createTask).mapN((a, b, c) => a+b+c).transact(xa).unsafeRunSync()
//

//  val insert: Int = sql"insert into tb_user (user_identification) values (${UUID.randomUUID().toString})".update.run.transact(xa).unsafeRunSync()

  def test = {
    val a = for {
      _ <- sql"insert into tb_user (user_identification) values (${UUID.randomUUID().toString})".update.run
      id <- sql"select lastval()".query[Int].unique
      u <- sql"select * from tb_user where id = $id".query[User].unique
    } yield u

    a.transact(xa).unsafeRunSync()
  }

//  def test(userIdentification: String) = sql"select * from tb_user where user_identification = ${userIdentification}"
//    .query[User]
//    .to[List]
//    .transact(xa)
//    .unsafeRunSync()
//    .take(100)
//    .foreach(println)

//  sql"select name from country"
//    .query[String]    // Query0[String]
//    .to[List]         // ConnectionIO[List[String]]
//    .transact(xa)     // IO[List[String]]
//    .unsafeRunSync    // List[String]
//    .take(5)          // List[String]
//    .foreach(println)

//  def ruun(userIdentification: String) = test(userIdentification).transact.unsafeRunSync()





}
