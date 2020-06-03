package repository.query

import java.time.{LocalDateTime, YearMonth}

import models.request._
import cats.implicits._
import doobie.implicits.javatime._
import doobie._
import models.responses._
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
        select count(t.id) as total_count,
  	   cast(avg(duration) as decimal(5,2)) as average_duration,
	     cast(AVG(cast(volume as decimal(5,2)))as decimal(5,2)) as average_volume,
	     cast(sum(duration*volume)/sum(volume)as decimal(5,2)) as weighted_average
      from tb_project p
      left join tb_task t on t.project_id = p.id
      inner join tb_user u on u.id = t.user_id
      where p.active = true
      and t.active = true""" ++
          projectNamesFilter ++
        dateRangeFilter
      ).query[OverallStatisticsReport]
  }
}