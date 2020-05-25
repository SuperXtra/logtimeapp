package data

import java.sql.Date
import java.time.ZonedDateTime

case class CreateProject(projectName: String, userIdentification: String)
case class ProjecReport(projectName: String)
case class ChangeProjectName(oldProjectName: String, projectName: String, userIdentification: String)
case class DeleteProject(projectName: String, userIdentification: String)
case class DeleteTask(taskDescription: String, projectName: String, userIdentification: String)
case class UpdateTask(oldTaskDescription: String,
                      userIdentification: String,
                      newTaskDescription: String,
                      startTime: Option[ZonedDateTime],
                      durationTime: Long,
                      volume: Option[Int],
                      comment: Option[String]
                     )
case class UpdateTaskInsert(
                             projectId: Long,
                             userId: Long,
                             taskDescription: String,
                             startTime: ZonedDateTime,
                             duration: Long,
                             volume: Option[Int],
                             comment: Option[String]
                           )

case class LogTask(
                        projectName: String,
                        userIdentification: String,
                        taskDescription: String,
                        startTime: ZonedDateTime,
                        durationTime: Long,
                        volume: Option[Int],
                        comment: Option[String]
                      )