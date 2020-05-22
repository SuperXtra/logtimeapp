package data

import akka.http.scaladsl.model.DateTime
import akka.http.scaladsl.model.DateTime
import cats.free.Free
import data.Entities.User
import doobie.free.connection
import doobie.{Fragment, Query0, Update0}
import doobie.implicits._

object Queries {

  object Project {

    def insert(project: ProjectData) = {
      val created = DateTime.now.toString()
      sql"insert into tb_project (user_id, project_name, create_time) VALUES (${project.user}, ${project.projectName}, $created)".update
    }

    def changeName(oldName: String, newName: String, requestingUserId: Int): Update0 = {
      fr"""
        update tb_project set project_name = ${newName}
        where project_name = ${oldName}
        and user_id = ${requestingUserId}
        """.update
    }

    def deleteProject(requestingUserId: Int, projectName: String, deleted: String): Update0 = {
      fr"""
        update tb_project set delete_time = ${deleted}
        where project_name = ${projectName}
        and user_id = ${requestingUserId}
        """.update
    }

    def getProject(projectName: String) = {
      fr"select * from tb_project where project_name = ${projectName}".query[Entities.Project]
    }

  }

  object Task {

    def insert(create: TaskRequest, projectId: Int, userId: Int): Update0 = {
      fr"""insert into tb_task (project_id, user_id, task_description, start_time, end_time, volume, comment) VALUES (
         ${projectId}
         ${userId}
         ${create.taskDescription}
         ${create.startTime.toString()}
         ${create.durationTime}
         ${create.volume}
         ${create.comment}
        )""".update
      //TODO:
      //add constraint that user cannot add new task where start time is between start and end of the other task
    }

    def deleteTask(taskDescription: String, projectId: Int) = {
      val created = DateTime.now.toString()

      fr"""
          update tb_task set delete_time = ${created}
          where project_id = ${projectId} and
          user_id = ${taskDescription}
          """.update

      // TODO:
      //add constraint that delete time cannot be bedore start time or end time

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

      def getUserId(userIdentification : String): Query0[User] = {
        fr"""
            select * from tb_user
            where user_identification = ${userIdentification}
            """.query[Entities.User]
      }

  }

}
