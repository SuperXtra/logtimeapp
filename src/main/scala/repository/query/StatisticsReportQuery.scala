package repository.query

import java.time.{LocalDateTime, YearMonth}

import akka.http.javadsl.model.DateTime
import models.model.{Ascending, ByCreatedTime, ByUpdateTime, Descending}
import models.request.{MainReport, ReportBodyWithParamsRequest}
import cats.implicits._
import doobie.implicits.javatime._
import doobie._
import doobie.util.query.Query0
import models.responses.{ReportFromDb, UserStatisticsReport}
import doobie.implicits._

object StatisticsReportQuery {

  def apply(request: MainReport) = {

    val projectNamesFilter: Fragment = request.userUUIDs match {
      case Some(uuids) => uuids match {
        case ::(_, _) =>
          fr"AND u.user_identification  IN (" ++
            uuids.map(x => fr"$x").intercalate(fr",") ++
            fr")"
        case Nil => fr""
      }
      case None => fr""
    }

    val dateRangeFilter: Fragment =
      (request.from, request.to) match {
        case (Some(from), Some(to)) =>
          fr"""AND t.start_time >= ${LocalDateTime.of(from.year, from.month, 1,0,0,0,0)} AND t.start_time <= ${LocalDateTime.of(to.year, to.month, YearMonth.of(to.year, to.month).lengthOfMonth(), 0, 0, 0, 0)}"""
        case (Some(from), None) => fr"""AND t.start_time >= ${LocalDateTime.of(from.year, from.month, 1,0,0,0,0)}"""
        case (None, Some(to)) => fr"""AND t.start_time <= ${LocalDateTime.of(to.year, to.month, YearMonth.of(to.year, to.month).lengthOfMonth(), 0, 0, 0, 0)}"""
        case (_, _) => fr""
      }


    (
      fr"""
         SELECT u.user_identification,
         COUNT (t.id) AS total_count,
         CAST(AVG(duration) AS INTEGER) AS average_duration,
         CAST(AVG(volume) AS INTEGER) AS average_volume,
         SUM(duration*volume)/SUM(volume) as weighted_average
         FROM tb_project p
         LEFT JOIN tb_task t
         ON t.project_id = p.id
         INNER JOIN tb_user u
         ON u.id = t.user_id
         WHERE p.active = true
         AND t.active = true""" ++
          projectNamesFilter ++
        dateRangeFilter ++
    fr"""
          GROUP BY u.user_identification
          """
      ).query[UserStatisticsReport]
  }
}