package models.request

import java.time.ZonedDateTime

import models.{TaskDuration, Volume}

case class DeleteTaskRequest(taskDescription: String,
                             projectName: String
                            )

case class UpdateTaskRequest(oldTaskDescription: String,
                             newTaskDescription: String,
                             startTime: Option[ZonedDateTime],
                             durationTime: TaskDuration,
                             volume: Option[Volume],
                             comment: Option[String]
                            )

case class LogTaskRequest(
                           projectName: String,
                           taskDescription: String,
                           startTime: ZonedDateTime,
                           durationTime: TaskDuration,
                           volume: Option[Volume],
                           comment: Option[String]
                         )