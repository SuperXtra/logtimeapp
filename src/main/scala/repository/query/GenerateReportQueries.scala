package repository.query


import models.model.{Ascending, ByCreatedTime, ByUpdateTime, Descending}
import models.request.ReportBodyWithParamsRequest
import cats.implicits._
import doobie.implicits.javatime._
import doobie._
import models.reports.ReportFromDb
import doobie.implicits._
import models.Active

object GenerateReportQueries {

  def apply(projectQuery: ReportBodyWithParamsRequest): doobie.Query0[ReportFromDb] = {

    val selectAllFromCTE =
      fr"""
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

    val projectNamesFilter: Fragment = projectQuery.params.ids match {
      case None => fr""
      case Some(Nil) => fr""
      case Some(projects) =>  fr"AND p.project_name IN (" ++
                              projects.map(x => fr"$x").intercalate(fr",") ++
                              fr")"
    }

    val dateRangeFilter: Fragment =

      (projectQuery.params.since, projectQuery.params.upTo) match {
        case (Some(from), Some(to)) => fr"""AND p.create_time BETWEEN TO_TIMESTAMP(${from.toLocalDateTime.toString}, 'YYYY-MM-DDTHH:xx:ss') AND TO_TIMESTAMP(${to.toLocalDateTime.toString}, 'YYYY-MM-DDTHH:xx:ss')"""
        case (Some(from), None) => fr"""AND p.create_time BETWEEN TO_TIMESTAMP(${from.toLocalDateTime}, 'YYYY-MM-DDTHH:xx:ss') AND TO_TIMESTAMP('2100-01-01 00:00:00', 'YYYY-MM-DDTHH:xx:ss')"""
        case (None, Some(to)) => fr"""AND p.create_time BETWEEN TO_TIMESTAMP('1900-01-01 00:00:00', 'YYYY-MM-DDTHH:xx:ss') AND TO_TIMESTAMP(${to.toLocalDateTime}, 'YYYY-MM-DDTHH:xx:ss')"""
        case (_, _) => fr""
      }

    val order: Fragment = projectQuery.pathParams.sortDirection match {
      case Some(value) => value match {
        case Ascending => fr"ASC"
        case Descending => fr"DESC"
        case _ => fr"ASC"
      }
      case None => fr""
    }

    val sortingCTE: Fragment = projectQuery.pathParams.projectSort match {
      case Some(value) => value match {
        case ByCreatedTime => fr" ORDER BY p.create_time ${order}"
        case ByUpdateTime => fr" ORDER BY update ${order}"
      }
      case None => fr""
    }

    val sortingSelect: Fragment = projectQuery.pathParams.projectSort.map {
      case ByCreatedTime => fr" ORDER BY p.create_time ${order}, t.create_time ${order}"
      case ByUpdateTime => fr" ORDER BY COALESCE(t.create_time, p.create_time) ${order}"
      }.getOrElse(fr"")

    val isActiveFilter = projectQuery.pathParams.active.map{
        case Active(value) if value => fr"AND COALESCE(t.active , true) = true"
        case Active(value) if !value => fr"AND COALESCE(t.active , false) = false"
      }.getOrElse(fr"")

    val paginationFilter = {
      val offset = ((projectQuery.pathParams.page.value -1 ) * projectQuery.pathParams.quantity.value).toLong
      val limitation = projectQuery.pathParams.quantity.value.toLong
      fr"""
        LIMIT ${limitation} OFFSET ${offset}
       """
    }

    val reportQuery =
      fr"""
        WITH projects_page AS(
        SELECT p.*, COALESCE(MAX(t.create_time), p.create_time) AS update
        FROM tb_project p
        LEFT JOIN tb_task t ON t.project_id = p.id
        WHERE 1 = 1
        """ ++
        projectNamesFilter ++
        isActiveFilter ++
        dateRangeFilter ++
        fr"""GROUP BY p.id""" ++
        sortingCTE ++
        paginationFilter ++
        fr")" ++
        selectAllFromCTE ++
        isActiveFilter ++
        sortingSelect

    reportQuery.query[ReportFromDb]
  }
}