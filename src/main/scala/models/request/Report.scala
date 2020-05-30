package models.request

import java.time.ZonedDateTime

import models.model.{Ascending, ByCreatedTime, ProjectSort, SortDirection}

// TODO do odzielnych plików
case class ReportBodyWithParamsRequest(
                                        params: ReportRequest,
                                        pathParams: ReportParams
                                      )

case class ReportRequest(
                          ids: Option[List[String]],
                          since: Option[ZonedDateTime],
                          upTo: Option[ZonedDateTime]
                        )

case class ReportParams(
                         active: Option[Boolean],
                         projectSort: Option[ProjectSort],
                         sortDirection: Option[SortDirection],
                         page: Int = 1,
                         quantity: Int = 20
                       )

case class MainReport(
                     userUUIDs: Option[List[String]],
                     from: Option[DateFilter],
                     to: Option[DateFilter]
                     )

case class DateFilter(
                     year: Int,
                     month: Int
                     )