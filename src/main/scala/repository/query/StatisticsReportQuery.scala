package repository.query

import java.time.{LocalDateTime, YearMonth}

import models.request._
import cats.implicits._
import slick.jdbc.{GetResult, PostgresProfile}
import doobie.implicits.javatime._
import models.reports._
import models.TotalCount
import slick.sql.SqlStreamingAction
import slick.jdbc.PostgresProfile.api._

object StatisticsReportQuery {

  implicit val getTotalCountResult: GetResult[TotalCount] = GetResult(r => TotalCount(r.<<))
  implicit val getOverallReportResult: GetResult[OverallStatisticsReport] = GetResult(r => OverallStatisticsReport(r.<<, r.<<, r.<<, r.<<))

  def apply(request: MainReport): SqlStreamingAction[Vector[OverallStatisticsReport], OverallStatisticsReport, PostgresProfile.api.Effect] = {

    val projectNamesFilter: String = request.userUUIDs match {
      case None => ""
      case Some(Nil) => ""
      case Some(uuidList) => " AND u.user_identification  IN (" ++ uuidList.map(x => s"'$x'").intercalate(",") ++ ")"
    }

    val dateRangeFilter =
      (request.from, request.to) match {
        case (Some(from), Some(to)) =>
          s"AND t.start_time >= '${LocalDateTime.of(from.year.value, from.month.value, 1,0,0,0,0)}' AND t.end_time <= '${LocalDateTime.of(to.year.value, to.month.value, YearMonth.of(to.year.value, to.month.value).lengthOfMonth(), 0, 0, 0, 0)}'"
        case (Some(from), None) => s"AND t.start_time >= '${LocalDateTime.of(from.year.value, from.month.value, 1,0,0,0,0)}'"
        case (None, Some(to)) => s"AND t.end_time <= '${LocalDateTime.of(to.year.value, to.month.value, YearMonth.of(to.year.value, to.month.value).lengthOfMonth(), 0, 0, 0, 0)}'"
        case (_, _) =>" "
      }

    val base = """SELECT COUNT(t.id) AS total_count,
    CAST(AVG(duration) AS decimal(5,2)) as average_duration,
    CAST(AVG(CAST(volume AS decimal(5,2))) AS decimal(5,2)) AS average_volume,
    CAST(SUM(duration*volume)/SUM(volume) AS decimal(5,2)) AS weighted_average
    FROM tb_project p
    LEFT join tb_task t ON t.project_id = p.id
    INNER JOIN tb_user u ON u.id = t.user_id
    WHERE p.active = true
    AND t.active = true """

    sql"""#$base #$projectNamesFilter #$dateRangeFilter""".as[OverallStatisticsReport]
  }
}