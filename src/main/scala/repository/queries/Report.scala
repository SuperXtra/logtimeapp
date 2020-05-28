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



//    val queryBody: Fragment =
//      fr"""
//          SELECT p.project_name, p.create_time as project_create_time, t.create_time as task_create_time, t.user_id, t.task_description, t.start_time, t.end_time, t.duration, t.volume, t.comment
//          FROM tb_project p
//          LEFT JOIN tb_task t ON p.id = t.project_id
//          WHERE 1 = 1
//          """


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

        val from = maybeFrom.map(_.toLocalDateTime.toString).orNull
        val to = maybeTo.map(_.toLocalDateTime.toString).orNull

        fr"AND p.create_time BETWEEN COALESCE(" ++
          fr"TO_TIMESTAMP($from, 'YYYY-MM-DD:hh:mm:ss'))" ++ fr"AND COALESCE(" ++ fr"TO_TIMESTAMP($to, 'YYYY-MM-DD:hh:mm:ss'))"
      }
    }

    val sort: Fragment = projectQuery.projectSort match {
      case ByCreatedTime => fr" ORDER BY project_create_time"
      case ByUpdateTime => fr" ORDER BY COALESCE(task_create_time, project_create_time)"
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

//      val start = (((page - 1) * limit) + 1).toLong
//      val end = (page * limit).toLong
//
//      fr"AND ranking IN (" ++
//        (start to end).toList.map(x => fr"$x").intercalate(fr",") ++
//        fr")"

      val offset = ((page-1)*limit).toLong
      val limitation = limit.toLong

      fr"""
          OFFSET ${offset} LIMIT ${limitation}
          """

    }


    val queryBody: Fragment = {

      fr"""
          WITH input_data AS (
            SELECT p.project_name, p.create_time AS project_create_time, t.create_time as task_create_time, t.user_id, t.task_description, t.start_time, t.end_time, t.duration, t.volume, t.comment,
            DENSE_RANK() OVER (ORDER BY p.id) AS ranking
            FROM tb_project p
            LEFT JOIN tb_task t ON p.id = t.project_id
            WHERE 1 = 1""" ++ filterIds ++ deletedFilter ++ filterDat ++
        fr""")
          SELECT project_name, project_create_time, task_create_time, user_id, task_description, start_time, end_time, duration, volume, comment, ranking
          FROM input_data
          WHERE 1=1
          """ ++ pagination ++ sort ++ desc
    }


    //    (queryBody ++ filterIds ++ deletedFilter ++ filterDat ++ sort ++ desc ++ pagination).query[FinalReport].to[List]
    queryBody.query[FinalReport].to[List]


  }

}
