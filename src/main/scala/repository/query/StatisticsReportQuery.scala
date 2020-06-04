package repository.query

import java.time.{LocalDateTime, YearMonth}

import models.request._
import cats.implicits._
import doobie.implicits.javatime._
import doobie._
import models.reports._
import doobie.implicits._

object StatisticsReportQuery {
  def apply(request: MainReport) = {
    val projectNamesFilter: Fragment = request.userUUIDs match {
      case None => fr""
      case Some(Nil) => fr""
      case Some(uuidList) =>  fr"AND u.user_identification  IN (" ++ uuidList.map(x => fr"$x").intercalate(fr",") ++ fr")"
      }

    val dateRangeFilter: Fragment =
      (request.from, request.to) match {
        case (Some(from), Some(to)) =>
          fr"""AND t.start_time >= ${LocalDateTime.of(from.year, from.month, 1,0,0,0,0)} AND t.end_time <= ${LocalDateTime.of(to.year, to.month, YearMonth.of(to.year, to.month).lengthOfMonth(), 0, 0, 0, 0)}"""
        case (Some(from), None) => fr"""AND t.start_time >= ${LocalDateTime.of(from.year, from.month, 1,0,0,0,0)}"""
        case (None, Some(to)) => fr"""AND t.end_time <= ${LocalDateTime.of(to.year, to.month, YearMonth.of(to.year, to.month).lengthOfMonth(), 0, 0, 0, 0)}"""
        case (_, _) => fr""
      }

    (
      fr"""
        SELECT COUNT(t.id) AS total_count,
  	    CAST(AVG(duration) AS decimal(5,2)) as average_duration,
	      CAST(AVG(CAST(volume AS decimal(5,2)))AS decimal(5,2)) AS average_volume,
	      CAST(SUM(duration*volume)/SUM(volume)AS decimal(5,2)) AS weighted_average
        FROM tb_project p
        LEFT join tb_task t ON t.project_id = p.id
        INNER JOIN tb_user u ON u.id = t.user_id
        WHERE p.active = true
        AND t.active = true""" ++
        projectNamesFilter ++
        dateRangeFilter
      ).query[OverallStatisticsReport]
  }
}