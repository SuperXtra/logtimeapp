package data

import akka.http.scaladsl.model.DateTime
import akka.http.scaladsl.model.DateTime
import cats.free.Free
import data.Entities.{Project, Task, User}
import doobie.{Query0, Update0}
import doobie.implicits._
import doobie.util.log.LogHandler
import java.sql.Timestamp
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

import doobie.implicits.javasql._
import doobie.implicits.javatime._


object Queries {
  implicit val han = LogHandler.jdkLogHandler

  object Project {

    def insert(projectName: String, userId: Long) = {
      sql"insert into tb_project (user_id, project_name, create_time) VALUES (${userId}, ${projectName}, ${Timestamp.valueOf(LocalDateTime.now())}) returning id".query[Long]
    }

    def changeName(oldName: String, newName: String, userId: Long): Update0 = {
      fr"""
        update tb_project set project_name = ${newName}
        where project_name = ${oldName}
        and user_id = ${userId}
        """.update
    }

    def deleteProject(requestingUserId: Long, projectName: String): Update0 = {
      fr"""
        update tb_project set delete_time = ${Timestamp.valueOf(LocalDateTime.now())}, active = false
        where project_name = ${projectName}
        and user_id = ${requestingUserId}
        """.update
    }

    def getProjectId(projectName: String) = {
      sql"select id from tb_project where project_name = ${projectName}".query[Long]
    }

    def getProject(projectName: String) = {
      sql"select * from tb_project where project_name = ${projectName}".query[Project]
    }

  }

  object Task {


    def insert(create: LogTask, projectId: Long, userId: Long) = {

      val start = LocalDateTime.parse(create.startTime)
      val end = start.plusMinutes(create.durationTime)

      sql"insert into tb_task (project_id, user_id, task_description, start_time, end_time, volume, comment) VALUES (${projectId}, ${userId}, ${create.taskDescription}, ${Timestamp.valueOf(start)}, ${Timestamp.valueOf(end)}, ${create.volume}, ${create.comment}) returning id".query[Long]
    }

    def selectLastInsertedTask(id: Long) = {
      sql"select * from tb_task where id = ${id}".query[Task]
    }

    def deleteTask(taskDescription: String, projectId: Int) = {
      val created = DateTime.now.toString()
      fr"""
          update tb_task set delete_time = ${created}
          where project_id = ${projectId} and
          user_id = ${taskDescription}
          """.update
    }

    def fetchTasksForProject(taskDescription: String, projectId: Int) = {
      fr"""
          select * from tb_task
          where task_description = ${taskDescription}
          and project_id = ${projectId}
          """.query[Entities.Task]
    }

  }

  object User {
      def insertUser(userIdentification : String) = {
        sql"insert into tb_user (user_identification) values (${userIdentification})".update
      }

    def selectLastInsertedUser() = {
      sql"select lastval()".query[Long]
    }

    def selectByUserIdentity(id: Int) = {
      sql"select * from tb_user where id = $id".query[User]
    }

      def getUserId(userIdentification : String): Query0[Long] = {
        fr"""
            select id from tb_user
            where user_identification = ${userIdentification}
            """.query[Long]
      }



  }

}
