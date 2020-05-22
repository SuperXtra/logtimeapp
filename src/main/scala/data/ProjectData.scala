package data

import akka.http.scaladsl.model.DateTime

case class ProjectData(projectName: String, user: Int)

case class TaskRequest(
                        taskDescription: String,
                        startTime: DateTime,
                        durationTime: Int,
                        volume: Option[Int],
                        comment: Option[String]
                      )
case class DeleteTaskRequest(

                            )
