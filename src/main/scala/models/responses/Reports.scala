package models.responses

import java.time.LocalDateTime

import models.model.{ProjectTb, TaskTb}


case class ProjectReport(
                          project: ProjectTb,
                          tasks: Tasks,
                          workedTimeInMinutes: Long
                        )

case class Tasks(tasks: List[TaskTb])

case class FinalReport(
                        project_name: String,
                        project_create_time: LocalDateTime,
                        task_create_time: LocalDateTime,
                        user_id: Int,
                        task_description: String,
                        start_time: LocalDateTime,
                        end_time: LocalDateTime,
                        duration: Int,
                        volume: Option[Int],
                        comment: Option[String]
                      )