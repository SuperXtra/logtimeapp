package models.model

import java.sql.Timestamp
import java.time._

import models._
import slick.ast.BaseTypedType
import slick.jdbc.JdbcType
import slick.jdbc.PostgresProfile.api._
import slick.lifted.ProvenShape


case class User(
                 userId: UserId = UserId(0),
                 userIdentification: String)


case class Task(
                 id: TaskId = TaskId(0),
                 projectId: ProjectId,
                 userId: UserId,
                 createTime: LocalDateTime,
                 taskDescription: String,
                 startTime: LocalDateTime,
                 endTime: LocalDateTime,
                 duration: TaskDuration,
                 volume: Option[Volume],
                 comment: Option[String],
                 deleteTime: Option[LocalDateTime],
                 active: Option[Active]
               )

case class Project(
                    id: ProjectId = ProjectId(0),
                    userId: UserId,
                    projectName: String,
                    createTime: LocalDateTime,
                    deleteTime: Option[LocalDateTime],
                    active: Option[Active]
                  )

object ProjectSchema {

  //TODO
  implicit val taskPKMapper =       MappedColumnType.base[TaskId, Int](_.value, TaskId)
  implicit val userPKMapper =       MappedColumnType.base[UserId, Int](_.value, UserId)
  implicit val projectPKMapper =    MappedColumnType.base[ProjectId, Int](_.value, ProjectId)
  implicit val activeMapper =       MappedColumnType.base[Active, Boolean](_.value, Active)
  implicit val taskDurationMapper = MappedColumnType.base[TaskDuration, Int](_.value, TaskDuration)
  implicit val volumeMapper =       MappedColumnType.base[Volume, Int](_.value, Volume)

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

  //TODO
  implicit val taskPKMapper =       MappedColumnType.base[TaskId, Int](_.value, TaskId)
  implicit val userPKMapper =       MappedColumnType.base[UserId, Int](_.value, UserId)
  implicit val projectPKMapper =    MappedColumnType.base[ProjectId, Int](_.value, ProjectId)
  implicit val activeMapper =       MappedColumnType.base[Active, Boolean](_.value, Active)
  implicit val taskDurationMapper = MappedColumnType.base[TaskDuration, Int](_.value, TaskDuration)
  implicit val volumeMapper =       MappedColumnType.base[Volume, Int](_.value, Volume)

  class UserTable(tag: Tag) extends Table[User](tag, "tb_user") {
    def userId = column[UserId]("id", O.PrimaryKey, O.AutoInc)
    def userIdentification = column[String]("user_identification")
    override def * : ProvenShape[User] = (userId, userIdentification) <> (User.tupled, User.unapply)
  }
  lazy val users = TableQuery[UserTable]
}

object TaskSchema {

  //TODO
  implicit val taskPKMapper =       MappedColumnType.base[TaskId, Int](_.value, TaskId)
  implicit val userPKMapper =       MappedColumnType.base[UserId, Int](_.value, UserId)
  implicit val projectPKMapper =    MappedColumnType.base[ProjectId, Int](_.value, ProjectId)
  implicit val activeMapper =       MappedColumnType.base[Active, Boolean](_.value, Active)
  implicit val taskDurationMapper = MappedColumnType.base[TaskDuration, Int](_.value, TaskDuration)
  implicit val volumeMapper =       MappedColumnType.base[Volume, Int](_.value, Volume)

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

object ColumnImplicits {
  implicit val taskPKMapper =       MappedColumnType.base[TaskId, Int](_.value, TaskId)
  implicit val userPKMapper =       MappedColumnType.base[UserId, Int](_.value, UserId)
  implicit val projectPKMapper =    MappedColumnType.base[ProjectId, Int](_.value, ProjectId)
  implicit val activeMapper =       MappedColumnType.base[Active, Boolean](_.value, Active)
  implicit val taskDurationMapper = MappedColumnType.base[TaskDuration, Int](_.value, TaskDuration)
  implicit val volumeMapper =       MappedColumnType.base[Volume, Int](_.value, Volume)
}