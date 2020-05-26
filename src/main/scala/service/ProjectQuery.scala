//package service
//
//import akka.http.scaladsl.model.DateTime
//import akka.http.scaladsl.model.DateTime
//import cats.implicits._
//import cats.effect._
//import data.Entities.{Project, Task, User}
//import doobie.{Query0, Update0}
//import doobie.implicits._
//import doobie.util.log.LogHandler
//import java.sql.Timestamp
//import java.time.LocalDateTime
//import java.util.concurrent.TimeUnit
//
//import data._
//import doobie.implicits.javasql._
//import doobie.implicits.javatime._
//import doobie.util.transactor.Transactor.Aux
//
//
//
//// TODO https://tpolecat.github.io/doobie/docs/08-Fragments.html
//class QueryProjects(con: Aux[IO, Unit])(implicit val contextShift: ContextShift[IO]) {
//  def apply(projectQuery: ProjectQuery, skip: Int, limit: Int) = {
//
//
//    //poczatkowa 1900-01-01
//    //koncowa  2100-01-01
//
//    val queryStart =
//      fr"""
//          SELECT *
//          FROM tb_project p
//          LEFT JOIN tb_task t ON p.id = t.project_id
//          WHERE 1 = 1
//          """
//
//    val queryBody =
//      fr"""SELECT
//          |   *,
//          |   COALESCE(XXX, 'nie podano') as update_date
//          |FROM tb_project p
//          |left JOIN tb_task t on p.id = t.id
//          |WHERE 1=1
//          |
//          |AND p.name IN ('','','')
//          |
//          |AND create_time between 'data' AND 'data2'
//          |
//          |""".stripMargin
//
//    //TODO left join chodzi o nulle
//    // TODO WHERE
//
////    val filterIds = projectQuery.ids.map(ids => s"AND p.name IN (${ids.mkString(",")}").getOrElse(fr"")
//
////    val filterId2 = emptyList match {
////      case _ :: Nil => ""
////      case _ :: _ => (emptyList.foldLeft("AND p.name IN (")((a,b) => a.concat(b + ","))).dropRight(1) + ")"
////    }
//
//
//
//    val deletedFilter: String = projectQuery.active match {
//      case Some(value) => value match {
//        case true => "AND active = true"
//        case false =>"AND active = false"
//      }
//      case None =>""
//    }
//
////    ids: Option[List[String]],
////    since: Option[Long],
////    upTo: Option[Long],
////    projectSort: Option[ProjectSort],
////    includeDeleted: Boolean = false,
////    sortDirection: SortDirection = Ascending
//
////    def test(values: (Option[String], Option[String])) ={
////      values match {
////        case (Some(maybeString), Some(maybeString1)) => s"AND create_time between coalesce(${maybeString}, '1900-01-01') AND COALESCE(${maybeString1}, '2100-01-01')"
////        case (None, Some(maybeString1)) => s"AND create_time between coalesce(null, '1900-01-01') AND COALESCE(${maybeString1}, '2100-01-01')"
////        case (Some(maybeString), None) => s"AND create_time between coalesce(${maybeString}, '1900-01-01') AND COALESCE(null, '2100-01-01')"
////        case (None, None) => ""
////      }
////    }
//
//    def filterDates(values: (Option[String], Option[String])) ={
//      values match {
//        case (maybeString, maybeString1) => s"AND create_time between coalesce(${maybeString.getOrElse("null")}, '1900-01-01') AND COALESCE(${maybeString1.getOrElse("null")}, '2100-01-01')"
//      }
//    }
//
//
//
//    val sort = projectQuery.projectSort match {
//      case Some(ByCreatedTime) => "ORDER BY p.created_date"
//      case Some(ByUpdateTime) => "ORDER BY " // TODO Justyna: COALESCE jakimś na left joinie
//      case None => fr""
//    }
//
////    val byCategory = auctionQuery.categoryId.map(cat => fr" AND categoryId = ${cat.underlying}").getOrElse(fr"")
//
//    val desc = projectQuery.sortDirection match {
//      case Some(Descending) => fr"DESC"
//      case _ => fr"ASC"
//    }
//
//    val queryLimit = s"LIMIT $skip, $limit"
//
//    // skip = 0, limit = 20
//    // skip = 20, limit = 20
//    // skip = 40, limit = 20
//
//    (queryBody ++ sort ++ desc ++ queryLimit).query[Project].transact(tx)
//  }
//}
