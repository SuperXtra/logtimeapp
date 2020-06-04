package models.reports

import java.time.LocalDateTime

import models.model.{Project, Task}


case class FinalProjectReport(
                          project: Project,
                          tasks: Tasks,
                          workedTimeInMinutes: Long
                        )

case class Tasks(tasks: List[Task])


case class FinalParametrizedReport(
                               project_name: Option[String],
                               project_create_time: Option[LocalDateTime],
                               tasks: List[ReportTask]
                             )

case class ReportTask(
                       task_create_time: Option[LocalDateTime],
                       task_description: Option[String],
                       start_time: Option[LocalDateTime],
                       end_time: Option[LocalDateTime],
                       duration: Option[Int],
                       volume: Option[Int],
                       comment: Option[String]
                     )

case class ReportFromDb(
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

case class OverallStatisticsReport(
                             total_count: Long,
                             average_duration: Option[BigDecimal],
                             average_volume: Option[BigDecimal],
                             weighted_average: Option[BigDecimal]
                           )