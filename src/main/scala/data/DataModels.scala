package data

import java.util.UUID

import akka.http.scaladsl.model.DateTime

case class CreateProject(projectName: String, userIdentification: String)
case class ProjecReport(projectName: String)
case class ChangeProjectName(oldProjectName: String, projectName: String, userIdentification: String)
case class DeleteProject(projectName: String, userIdentification: String)
case class DeleteTask(taskDescription: String, projectName: String, userIdentification: String)
case class UpdateTask(oldTaskDescription: String,
                      userIdentification: String,
                      newTaskDescription: String,
                      startTime: Option[String],
                      durationTime: Long,
                      volume: Option[Int],
                      comment: Option[String]
                     )
case class UpdateTaskInsert(
                             projectId: Int,
                             userId: Int,
                             taskDescription: String,
                             startTime: String,
                             duration: Long,
                             volume: Option[Int],
                             comment: Option[String]
                           )

case class LogTask(
                        projectName: String,
                        userIdentification: String,
                        taskDescription: String,
                        startTime: String,
                        durationTime: Long,
                        volume: Option[Int],
                        comment: Option[String]
                      )