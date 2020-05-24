package data

import akka.http.scaladsl.model.DateTime

case class CreateProject(projectName: String, userIdentification: String)
case class ChangeProjectName(oldProjectName: String, projectName: String, userIdentification: String)
case class DeleteProject(projectName: String, userIdentification: String)

case class LogTask(
                        projectName: String,
                        userIdentification: String,
                        taskDescription: String,
                        startTime: String,
                        durationTime: Long,
                        volume: Option[Int],
                        comment: Option[String]
                      )

case class DeleteTaskRequest(

                            )

