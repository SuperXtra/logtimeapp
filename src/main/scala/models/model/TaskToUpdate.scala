package models.model

import java.time.ZonedDateTime

import models._

case class TaskToUpdate(
                         projectId: ProjectId,
                         userId: UserId,
                         taskDescription: String,
                         startTime: ZonedDateTime,
                         duration: TaskDuration,
                         volume: Option[Volume],
                         comment: Option[String]
                       )