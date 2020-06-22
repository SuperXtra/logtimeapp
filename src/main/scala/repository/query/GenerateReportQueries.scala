package repository.query

import models.model._
import models.request.ReportBodyWithParamsRequest
import cats.implicits._
import models.reports._
import models._
import slick.jdbc.{GetResult, PositionedResult}
import slick.jdbc.PostgresProfile.api._

object GenerateReportQueries {

  def apply(projectQuery: ReportBodyWithParamsRequest) = {
    implicit val localDateTime = GetResult.apply((x: PositionedResult) => x.nextTimestampOption().map(_.toLocalDateTime))
    implicit val getOverallReportResult = GetResult(r => ReportFromDb(r.<<, r.<<, r.<<, r.<<, r.<<, r.<<, r.<<, r.<<, r.<<))

    val base =
      """ WITH projects_page AS(
                   SELECT p.*, COALESCE(MAX(t.create_time), p.create_time) AS update
                   FROM tb_project p
                   LEFT JOIN tb_task t ON t.project_id = p.id
                   WHERE 1 = 1"""

    val selectAllFromCTE =
      """
           SELECT
            p.project_name,
            p.create_time as project_create_time,
            t.create_time as task_create_time,
            t.task_description as task_description,
            t.start_time,
            t.end_time,
            t.duration,
            t.volume,
            t.comment
           FROM projects_page p
           left join tb_task t on t.project_id = p.id
           WHERE 1=1
          """

    val projectNamesFilter = projectQuery.params.ids match {
      case None => " "
      case Some(Nil) => " "
      case Some(projects) => " AND p.project_name IN ( " +
        projects.map(x => s"'$x'").intercalate(",") +
        " )"
    }

    val dateRangeFilter =

      (projectQuery.params.since, projectQuery.params.upTo) match {
        case (Some(from), Some(to)) => s" AND p.create_time BETWEEN TO_TIMESTAMP('${from.toLocalDateTime.toString}', 'YYYY-MM-DDTHH:xx:ss') AND TO_TIMESTAMP('${to.toLocalDateTime.toString}', 'YYYY-MM-DDTHH:xx:ss')"
        case (Some(from), None) => s" AND p.create_time BETWEEN TO_TIMESTAMP('${from.toLocalDateTime}', 'YYYY-MM-DDTHH:xx:ss') AND TO_TIMESTAMP('2100-01-01 00:00:00', 'YYYY-MM-DDTHH:xx:ss')"
        case (None, Some(to)) => s" AND p.create_time BETWEEN TO_TIMESTAMP('1900-01-01 00:00:00', 'YYYY-MM-DDTHH:xx:ss') AND TO_TIMESTAMP('${to.toLocalDateTime}', 'YYYY-MM-DDTHH:xx:ss')"
        case (_, _) => " "
      }

    val order = projectQuery.pathParams.sortDirection match {
      case Some(value) => value match {
        case Ascending => " ASC"
        case Descending => " DESC"
        case _ => " ASC"
      }
      case None => " "
    }

    val sortingCTE = projectQuery.pathParams.projectSort match {
      case Some(value) => value match {
        case ByCreatedTime => s" ORDER BY p.create_time ${order}"
        case ByUpdateTime => s" ORDER BY update ${order}"
      }
      case None => " "
    }

    val sortingSelect = projectQuery.pathParams.projectSort.map {
      case ByCreatedTime => s" ORDER BY p.create_time ${order}, t.create_time ${order}"
      case ByUpdateTime => s" ORDER BY COALESCE(t.create_time, p.create_time) ${order}"
    }.getOrElse(" ")

    val isActiveFilter = projectQuery.pathParams.active.map {
      case Active(value) if value => " AND COALESCE(t.active , true) = true"
      case Active(value) if !value => " AND COALESCE(t.active , false) = false"
    }.getOrElse(" ")

    val paginationFilter = {
      val offset = ((projectQuery.pathParams.page.value - 1) * projectQuery.pathParams.quantity.value).toLong
      val limitation = projectQuery.pathParams.quantity.value.toLong
      s"""
        LIMIT ${limitation} OFFSET ${offset}
       """
    }

    sql"""#$base #$projectNamesFilter #$isActiveFilter #$dateRangeFilter GROUP BY p.id #$sortingCTE #$paginationFilter ) #$selectAllFromCTE #$isActiveFilter #$sortingSelect""".as[ReportFromDb]

  }
}