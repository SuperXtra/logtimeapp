package repository.query

import java.time.{LocalDateTime, YearMonth}

import models.request._
import cats.implicits._
import slick.jdbc.{GetResult, PostgresProfile}
import doobie.implicits.javatime._
import doobie._
import models.reports._
import doobie.implicits._
import models.TotalCount
import models.model.{ProjectSchema, TaskSchema, UserSchema}
import slick.sql.SqlStreamingAction
import utils.SlickPlainSqlActionBuilderSyntax

object StatisticsReportQuery {

  def apply(request: MainReport): SqlStreamingAction[Vector[OverallStatisticsReport], OverallStatisticsReport, PostgresProfile.api.Effect] = {
    val projectNamesFilter = request.userUUIDs match {
      case None => fr""
      case Some(Nil) => fr""
      case Some(uuidList) =>  fr"AND u.user_identification  IN (" ++ uuidList.map(x => fr"$x").intercalate(fr",") ++ fr")"
      }

    val projectNamesFilterSlick: String = request.userUUIDs match {
      case None => ""
      case Some(Nil) => ""
      case Some(uuidList) => " AND u.user_identification  IN (" ++ uuidList.map(x => s"'$x'").intercalate(",") ++ ")"
    }

    val dateRangeFilter: Fragment =
      (request.from, request.to) match {
        case (Some(from), Some(to)) =>
          fr"""AND t.start_time >= ${LocalDateTime.of(from.year.value, from.month.value, 1,0,0,0,0)} AND t.end_time <= ${LocalDateTime.of(to.year.value, to.month.value, YearMonth.of(to.year.value, to.month.value).lengthOfMonth(), 0, 0, 0, 0)}"""
        case (Some(from), None) => fr"""AND t.start_time >= ${LocalDateTime.of(from.year.value, from.month.value, 1,0,0,0,0)}"""
        case (None, Some(to)) => fr"""AND t.end_time <= ${LocalDateTime.of(to.year.value, to.month.value, YearMonth.of(to.year.value, to.month.value).lengthOfMonth(), 0, 0, 0, 0)}"""
        case (_, _) => fr""
      }



    import slick.jdbc.PostgresProfile.api._


    val dateRangeFilterSlick =
      (request.from, request.to) match {
        case (Some(from), Some(to)) =>
          s"AND t.start_time >= '${LocalDateTime.of(from.year.value, from.month.value, 1,0,0,0,0)}' AND t.end_time <= '${LocalDateTime.of(to.year.value, to.month.value, YearMonth.of(to.year.value, to.month.value).lengthOfMonth(), 0, 0, 0, 0)}'"
        case (Some(from), None) => s"AND t.start_time >= '${LocalDateTime.of(from.year.value, from.month.value, 1,0,0,0,0)}'"
        case (None, Some(to)) => s"AND t.end_time <= '${LocalDateTime.of(to.year.value, to.month.value, YearMonth.of(to.year.value, to.month.value).lengthOfMonth(), 0, 0, 0, 0)}'"
        case (_, _) =>" "
      }

//    val ssss = (projects joinLeft tasks  on (_.id === _.projectId)) join users on (_._2.map(_.userId) == _.userId)

    val base = """SELECT COUNT(t.id) AS total_count,
    CAST(AVG(duration) AS decimal(5,2)) as average_duration,
    CAST(AVG(CAST(volume AS decimal(5,2))) AS decimal(5,2)) AS average_volume,
    CAST(SUM(duration*volume)/SUM(volume) AS decimal(5,2)) AS weighted_average
    FROM tb_project p
    LEFT join tb_task t ON t.project_id = p.id
    INNER JOIN tb_user u ON u.id = t.user_id
    WHERE p.active = true
    AND t.active = true """


    implicit val getTotalCountResult = GetResult(r => TotalCount(r.<<))
    implicit val getOverallReportResult = GetResult(r => OverallStatisticsReport(r.<<, r.<<, r.<<, r.<<))


    sql"""#$base #$projectNamesFilterSlick #$dateRangeFilterSlick""".as[OverallStatisticsReport]
  }
}