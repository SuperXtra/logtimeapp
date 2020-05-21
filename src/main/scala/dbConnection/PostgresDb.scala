package dbConnection

import doobie._
import doobie.implicits._
import doobie.util.ExecutionContexts
import cats._
import cats.data._
import cats.effect._
import cats.implicits._
import com.typesafe.config.ConfigFactory
import config.DatabaseConfig
import pureconfig._
import pureconfig.generic.auto._

class PostgresDb {

  val config = ConfigFactory.load("database-config.conf")
  val databaseConfig = ConfigSource.fromConfig(config).loadOrThrow[DatabaseConfig]

  implicit val cs = IO.contextShift(ExecutionContexts.synchronous)

  val xa = Transactor.fromDriverManager[IO](
    databaseConfig.driver,
    databaseConfig.url,
    databaseConfig.userName,
    databaseConfig.password,
    Blocker.liftExecutionContext(ExecutionContexts.synchronous) // just for testing
  )

//  val y = xa.yolo
//  import y._

  val createUser =
    sql"""
         create table if not exists user (
            id serial primary key,
            user_identification varchar(50) unique
          )
         """.update.run

  val createProject =
    sql"""
         create table if not exists project (
            id serial primary key,
            user_id integer,
            project_name varchar (255) unique not null,
            create_time timestamp not null,
            delete_time timestamp default null,
            foreign key (user_id) references user (id)
            )
         """.update.run

  val createTask =
    sql"""
         create table if not exists task (
            id serial primary key,
            project_id integer,
            user_id integer,
            task_description varchar (255) not null,
            start_time timestamp not null,
            end_time timestamp,
            volume integer,
            comment varchar (255),
            delete_time timestamp default null,
            foreign key (project_id) references project (id),
            foreign key (user_id) references user (id),
            constraint uq_project_task_desc unique (project_id, task_description)
         )
         """.update.run

  (createUser, createProject, createTask).mapN((a, b, c) => a+b+c).transact(xa).unsafeRunSync()

}
