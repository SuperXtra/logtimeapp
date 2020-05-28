package models.request

import java.time.ZonedDateTime

import models.model.{Ascending, ByCreatedTime, ProjectSort, SortDirection}

// TODO do odzielnych plików
case class ReportRequest(
                           params: RRequest,
                           path: RReq
                         )

case class RRequest(
                     ids: Option[List[String]],
                     since: Option[ZonedDateTime],
                     upTo: Option[ZonedDateTime]
                   )
case class RReq (
                  active: Option[Boolean],
                  projectSort: Option[ProjectSort],
                  sortDirection: Option[SortDirection],
                  page: Int,
                  quantity: Int
                )
