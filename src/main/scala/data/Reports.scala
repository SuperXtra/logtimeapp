package data

import java.time.ZonedDateTime


// TODO do odzielnych plików
case class ProjectQuery(
                         ids: Option[List[String]],
                         since: Option[ZonedDateTime],
                         upTo: Option[ZonedDateTime],
                         projectSort: ProjectSort = ByCreatedTime,
                         active: Option[Boolean],
                         sortDirection: SortDirection = Ascending,
                         page: Int,
                         quantity: Int
                       )

// TODO do odzielnych plików
sealed trait ProjectSort extends Product with Serializable
case object ByCreatedTime extends ProjectSort
case object ByUpdateTime extends ProjectSort

// TODO do odzielnych plików
sealed trait SortDirection  extends Product with Serializable
case object Ascending extends SortDirection
case object Descending extends SortDirection