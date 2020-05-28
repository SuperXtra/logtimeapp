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
                        project_name: Option[String],
                        project_create_time: Option[LocalDateTime],
                        task_create_time: Option[LocalDateTime],
                        task_description: Option[String],
                        start_time: Option[LocalDateTime],
                        end_time: Option[LocalDateTime],
                        duration: Option[Int],
                        volume: Option[Int],
                        comment: Option[String]
                      )

case class Repoooort (
                       project_name: Option[String],
                       project_create_time: Option[LocalDateTime],
                       tasks: List[RepTasks]
                     )

case class RepTasks (
                      task_create_time: Option[LocalDateTime],
                      task_description: Option[String],
                      start_time: Option[LocalDateTime],
                      end_time: Option[LocalDateTime],
                      duration: Option[Int],
                      volume: Option[Int],
                      comment: Option[String]
                    )