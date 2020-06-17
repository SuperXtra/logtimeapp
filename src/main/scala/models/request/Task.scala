package models.request

import java.time.ZonedDateTime

case class DeleteTaskRequest(taskDescription: String,
                             projectName: String
                            )

case class UpdateTaskRequest(oldTaskDescription: String,
                             newTaskDescription: String,
                             startTime: Option[ZonedDateTime],
                             durationTime: Long,
                             volume: Option[Int],
                             comment: Option[String]
                            )

case class LogTaskRequest(
                           projectName: String,
                           taskDescription: String,
                           startTime: ZonedDateTime,
                           durationTime: Long,
                           volume: Option[Int],
                           comment: Option[String]
                         )