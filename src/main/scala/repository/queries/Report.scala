package repository.queries

import models.model.{Ascending, ByCreatedTime, ByUpdateTime, Descending}
import models.request.ReportRequest
import models.responses.FinalReport
import java.time.{ZoneOffset, ZonedDateTime}

import cats.implicits._
import doobie.free.connection.ConnectionIO
import doobie.util.log.LogHandler
import doobie.implicits.javatime._
import doobie.{Fragment, Update0}
import doobie.implicits._
import doobie.util.query.Query0
import models.model._
import models.request._
import models.responses.FinalReport

object Report {
  implicit val han = LogHandler.jdkLogHandler


  def apply(projectQuery: ReportRequest, page: Int =1, limit: Int = 20) = {


    val queryBody: Fragment =
      fr"""
          SELECT p.project_name, p.create_time,
          t.user_id, t.task_description, t.start_time, t.end_time, t.duration, t.volume, t.comment
          FROM tb_project p
          LEFT JOIN tb_task t ON p.id = t.project_id
          WHERE 1 = 1
          """


    val filterIds: Fragment = projectQuery.ids match {
      case Some(projects) => projects match {
        case ::(_, _) =>
          fr"AND p.project_name IN (" ++
            projects.map(x => fr"$x").intercalate(fr",") ++
            fr")"
        case Nil => fr""
      }
      case None => fr""
    }


    val filterDat: Fragment = (projectQuery.since, projectQuery.upTo) match {
      case (maybeFrom, maybeTo) =>{

        val from = maybeFrom.map(_.toLocalDateTime).orNull
        val to = maybeTo.map(_.toLocalDateTime).orNull

        fr"AND create_time BETWEEN COALESCE(" ++
          fr"TO_TIMESTAMP($from, 'YYYY-MM-DD:hh:mm:ss'))" ++ fr"AND COALESCE(" ++ fr"TO_TIMESTAMP($to, 'YYYY-MM-DD:hh:mm:ss'))"
      }
    }

    val sort: Fragment = projectQuery.projectSort match {
      case ByCreatedTime => fr" ORDER BY p.created_date"
      case ByUpdateTime => fr" ORDER BY COALESCE(t.created_date, p.created_date)"
    }

    //    val byCategory = auctionQuery.categoryId.map(cat => fr" AND categoryId = ${cat.underlying}").getOrElse(fr"")

    val desc: Fragment = projectQuery.sortDirection match {
      case Ascending => fr"ASC"
      case Descending => fr"DESC"
      case _ => fr"ASC"
    }

    val deletedFilter = projectQuery.active match {
      case Some(value) => value match {
        case true => fr"AND t.active = true"
        case false => fr"AND t.active = false"
      }
      case None => fr""
    }

    val pagination = {

      val start = ((page - 1) * limit) + 1
      val end = page * limit

      fr"AND range IN (" ++
        (start to end).toList.map(x => fr"$x").intercalate(fr",") ++
        fr")"
    }
    //          fr"AND range IN (" ++

    (queryBody ++ filterIds ++ deletedFilter ++ filterDat ++ pagination ++ sort ++ desc).query[FinalReport]


  }

}
