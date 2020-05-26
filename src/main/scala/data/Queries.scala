package data

import java.sql.Timestamp

import data.Entities.{Project, Task, User}
import doobie._
import doobie.implicits._
import java.time.{LocalDateTime, ZoneOffset, ZonedDateTime}
import cats._, cats.data._, cats.implicits._
import doobie.util.log.LogHandler
import doobie.implicits.javatime._
import doobie.implicits.javasql._


object Queries {
  implicit val han = LogHandler.jdkLogHandler

  object Project {


    def insert(projectName: String, userId: Long) = {
      sql"insert into tb_project (user_id, project_name, create_time) VALUES (${userId}, ${projectName}, ${ZonedDateTime.now(ZoneOffset.UTC).toLocalDateTime}) returning id".query[Long].unique
    }

    def changeName(oldName: String, newName: String, userId: Long): Update0 = {
      fr"""
        update tb_project set project_name = ${newName}
        where project_name = ${oldName}
        and user_id = ${userId}
        """.update
    }

    def deleteProject(requestingUserId: Long, projectName: String, deleteTime: ZonedDateTime): Update0 = {
      fr"""UPDATE tb_project
           SET delete_time = ${deleteTime.toLocalDateTime}, active = false
           WHERE project_name = $projectName
           AND user_id = $requestingUserId
           """
        .update
    }

    def getProjectId(projectName: String) = {
      sql"select id from tb_project where project_name = ${projectName}".query[Long]
    }

    def getProject(projectName: String) = {
      sql"select * from tb_project where project_name = ${projectName}".query[Project].option
    }

    def projectExists(projectName: String) = {
      sql"select exists(select * from tb_project where project_name = ${projectName})".query[Boolean]
    }


    object Report {

      def apply(projectQuery: ProjectQuery, page: Int =1, limit: Int = 20) = {


        val queryBody: Fragment =
          fr"""
          SELECT p.project_name, p.create_time,
          t.user_id, t.task_description, t.start_time, t.end_time, t.duration, t.volume, t.comment
          FROM tb_project p
          LEFT JOIN tb_task t ON p.id = t.project_id
          WHERE 1 = 1
          """


        val filterIds: Fragment = projectQuery.ids match {
          case Some(projects) => projects match {
            case ::(_, _) =>
              fr"AND p.project_name IN (" ++
                projects.map(x => fr"$x").intercalate(fr",") ++
                fr")"
            case Nil => fr""
          }
          case None => fr""
        }


        val filterDat: Fragment = (projectQuery.since, projectQuery.upTo) match {
          case (maybeFrom, maybeTo) =>{

            val from = maybeFrom.map(_.toLocalDateTime).orNull
            val to = maybeTo.map(_.toLocalDateTime).orNull

            fr"AND create_time BETWEEN COALESCE(" ++
            fr"TO_TIMESTAMP($from, 'YYYY-MM-DD:hh:mm:ss'))" ++ fr"AND COALESCE(" ++ fr"TO_TIMESTAMP($to, 'YYYY-MM-DD:hh:mm:ss'))"
          }
        }

        val sort: Fragment = projectQuery.projectSort match {
          case ByCreatedTime => fr" ORDER BY p.created_date"
          case ByUpdateTime => fr" ORDER BY COALESCE(t.created_date, p.created_date)"
        }

        //    val byCategory = auctionQuery.categoryId.map(cat => fr" AND categoryId = ${cat.underlying}").getOrElse(fr"")

        val desc: Fragment = projectQuery.sortDirection match {
          case Ascending => fr"ASC"
          case Descending => fr"DESC"
          case _ => fr"ASC"
        }

        val deletedFilter = projectQuery.active match {
          case Some(value) => value match {
            case true => fr"AND t.active = true"
            case false => fr"AND t.active = false"
          }
          case None => fr""
        }

        val pagination = {

          val start = ((page - 1) * limit) + 1
          val end = page * limit

          fr"AND range IN (" ++
          (start to end).toList.map(x => fr"$x").intercalate(fr",") ++
            fr")"
        }
        //          fr"AND range IN (" ++

        (queryBody ++ filterIds ++ deletedFilter ++ filterDat ++ pagination ++ sort ++ desc).query[FinalReport]


      }

    }


  }

  object Task {
    def insertUpdate(update: UpdateTaskInsert) = {
      val start = update.startTime
      val end = start.plusMinutes(update.duration)
      sql"insert into tb_task (project_id, user_id, task_description, start_time, end_time, duration, volume, comment) VALUES (${update.projectId}, ${update.userId}, ${update.taskDescription}, ${start.toLocalDateTime}, ${end.toLocalDateTime}, ${update.duration}, ${update.volume}, ${update.comment}) returning id".query[Long].option

    }


    def insert(create: LogTaskModel, projectId: Long, userId: Long) = {
      val start = create.startTime.withZoneSameInstant(ZoneOffset.UTC)
      val end: ZonedDateTime = start.plusMinutes(create.durationTime)
      sql"insert into tb_task (project_id, user_id, task_description, start_time, end_time, duration, volume, comment) VALUES (${projectId}, ${userId}, ${create.taskDescription}, ${start.toLocalDateTime}, ${end.toLocalDateTime}, ${create.durationTime}, ${create.volume}, ${create.comment}) returning id".query[Long].unique
    }

    def selectLastInsertedTask(id: Long) = {
      sql"select * from tb_task where id = ${id}".query[Task].option
    }

    def deleteTask(taskDescription: String, projectId: Long, userId: Long) = {
      val created = ZonedDateTime.now(ZoneOffset.UTC).toLocalDateTime
      fr"""
          update tb_task set delete_time = ${created}, active = false
          where project_id = ${projectId} and
          user_id = ${userId}
          and task_description = ${taskDescription}
          and active = true
          """.update
    }

    def fetchTasksForProject(projectId: Long) = {
      fr"""
          select * from tb_task
          where project_id = ${projectId}
          and active = true
          """.query[Task].to[List]
    }

    def fetchTask(taskDescription: String, userId: Long) = {
      sql"select * from tb_task where task_description = ${taskDescription} and user_id = ${userId} and active = true".query[Task].option
    }

    def deleteTasksForProject(projectId: Long, deleteTime: ZonedDateTime): Update0 = {
      fr"""
        update tb_task set delete_time = ${deleteTime.toLocalDateTime}, active = false
        where project_id = ${projectId}
        """.update
    }

  }

  object User {
    def insertUser(userIdentification: String) = {
      sql"insert into tb_user (user_identification) values (${userIdentification}) returning id".query[Long].option
    }

    def selectLastInsertedUser() = {
      sql"select lastval()".query[Long]
    }

    def selectByUserIdentity(id: Long) = {
      sql"select * from tb_user where id = $id".query[User].option
    }

    def getUserId(userIdentification: String) = {
      fr"""
            select id from tb_user
            where user_identification = ${userIdentification}
            """.query[Long].option
    }

    def userExists(userIdentification: String) = {
      sql"select exists(select * from tb_user where user_identification = ${userIdentification})".query[Boolean]
    }
  }

}
