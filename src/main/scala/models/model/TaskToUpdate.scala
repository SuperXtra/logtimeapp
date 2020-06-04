package models.model

import java.time.ZonedDateTime

case class TaskToUpdate(
                         projectId: Long,
                         userId: Long,
                         taskDescription: String,
                         startTime: ZonedDateTime,
                         duration: Long,
                         volume: Option[Int],
                         comment: Option[String]
                       )