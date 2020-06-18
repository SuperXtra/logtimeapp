package models.model

import java.time._

import models._

case class User(
                 userId: UserId,
                 userIdentification: String)


case class Task(
                 id: TaskId,
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
                    id: ProjectId,
                    userId: UserId,
                    projectName: String,
                    createTime: LocalDateTime,
                    deleteTime: Option[LocalDateTime],
                    active: Option[Active]
                  )