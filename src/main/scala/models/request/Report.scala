package models.request

import java.time.ZonedDateTime

import models.{Active, Month, Page, Quantity, Year}
import models.model.{Ascending, ByCreatedTime, ProjectSort, SortDirection}

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
                         active: Option[Active],
                         projectSort: Option[ProjectSort],
                         sortDirection: Option[SortDirection],
                         page: Page,
                         quantity: Quantity
                       )

case class MainReport(
                     userUUIDs: Option[List[String]],
                     from: Option[DateFilter],
                     to: Option[DateFilter]
                     )

case class DateFilter(
                       year: Year,
                       month: Month
                     )