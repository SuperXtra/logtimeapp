package db

import java.time.LocalDateTime
import models.{Active, ProjectId, TaskDuration, TaskId, UserId, Volume}
import models.model.{Project, Task, User}
import slick.jdbc.PostgresProfile.api._
import slick.lifted.ProvenShape

object ProjectSchema {
  import ColumnImplicits._

  class ProjectTable(tag: Tag) extends Table[Project](tag, "tb_project") {
    def id = column[ProjectId]("id", O.PrimaryKey, O.AutoInc)
    def userId = column[UserId]("user_id")
    def projectName = column[String]("project_name")
    def createTime = column[LocalDateTime]("create_time")
    def deleteTime = column[Option[LocalDateTime]]("delete_time")
    def active = column[Option[Active]]("active")
    def * = (id, userId, projectName, createTime, deleteTime, active) <> (Project.tupled, Project.unapply)
  }
  lazy val projects = TableQuery[ProjectTable]
}

object UserSchema {
  import ColumnImplicits._

  class UserTable(tag: Tag) extends Table[User](tag, "tb_user") {
    def userId = column[UserId]("id", O.PrimaryKey, O.AutoInc)
    def userIdentification = column[String]("user_identification")
    override def * : ProvenShape[User] = (userId, userIdentification) <> (User.tupled, User.unapply)
  }
  lazy val users = TableQuery[UserTable]
}

object TaskSchema {
  import ColumnImplicits._

  class TaskTable(tag: Tag) extends Table[Task](tag, "tb_task") {
    def id = column[TaskId]("id", O.PrimaryKey, O.AutoInc)
    def projectId = column[ProjectId]("project_id")
    def userId = column[UserId]("user_id")
    def createTime = column[LocalDateTime]("create_time")
    def taskDescription = column[String]("task_description")
    def startTime = column[LocalDateTime]("start_time")
    def endTime = column[LocalDateTime]("end_time")
    def duration = column[TaskDuration]("duration")
    def volume = column[Option[Volume]]("volume")
    def comment = column[Option[String]]("comment")
    def deleteTime = column[Option[LocalDateTime]]("delete_time")
    def active = column[Option[Active]]("active")

    override def * : ProvenShape[Task] =
      ( id, projectId, userId, createTime,
        taskDescription, startTime, endTime, duration,
        volume, comment, deleteTime, active
      ) <> (Task.tupled, Task.unapply)
  }

  lazy val tasks = TableQuery[TaskTable]
}