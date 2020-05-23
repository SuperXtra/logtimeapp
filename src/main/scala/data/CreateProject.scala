package data

import akka.http.scaladsl.model.DateTime

case class CreateProject(projectName: String, userIdentification: String)
case class ChangeProjectName(oldProjectName: String, projectName: String, userIdentification: String)


case class TaskRequest(
                        taskDescription: String,
                        startTime: DateTime,
                        durationTime: Int,
                        volume: Option[Int],
                        comment: Option[String]
                      )
case class DeleteTaskRequest(

                            )

