package models.request

import java.time.ZonedDateTime

case class DeleteTaskRequest(taskDescription: String, projectName: String, userIdentification: String)
case class UpdateTaskRequest(oldTaskDescription: String,
                             userIdentification: String,
                             newTaskDescription: String,
                             startTime: Option[ZonedDateTime],
                             durationTime: Long,
                             volume: Option[Int],
                             comment: Option[String]
                            )


case class LogTaskRequest(
                           projectName: String,
                           userIdentification: String,
                           taskDescription: String,
                           startTime: ZonedDateTime,
                           durationTime: Long,
                           volume: Option[Int],
                           comment: Option[String]
                         )