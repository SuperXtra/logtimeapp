package models.model

import java.time._
import models._

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