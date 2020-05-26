package models.request

import java.time.ZonedDateTime

import models.model.{Ascending, ByCreatedTime, ProjectSort, SortDirection}

// TODO do odzielnych plików
case class ReportRequest(
                           ids: Option[List[String]],
                           since: Option[ZonedDateTime],
                           upTo: Option[ZonedDateTime],
                           projectSort: ProjectSort = ByCreatedTime,
                           active: Option[Boolean],
                           sortDirection: SortDirection = Ascending,
                           page: Int,
                           quantity: Int
                         )
