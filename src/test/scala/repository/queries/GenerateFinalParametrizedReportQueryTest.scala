package repository.queries

import java.time.{LocalDateTime, ZoneOffset, ZonedDateTime}

import akka.http.scaladsl.model.DateTime
import models.request.{ReportBodyWithParamsRequest, ReportParams, ReportRequest}
import repository.query.GenerateReportQueries

class GenerateFinalParametrizedReportQueryTest extends QueryTest {

  test("Report test") {
    GenerateReportQueries(ReportBodyWithParamsRequest(ReportRequest(Some(List("1", "test")), None,None), ReportParams(None,None,None,page=1, quantity = 20)))
  }

  test("") {

    val startYeat = 2020
    val startMonth = 2
    val endYear = 2025
    val endMonth = 5

    DateTime

    val dateString = "2020-05"
    val format = "-01T00:00:00"
    println(LocalDateTime.parse(dateString+format).atZone(ZoneOffset.UTC))

    val from = LocalDateTime.of(startYeat, startMonth,1,0,0, 0).atZone(ZoneOffset.UTC).toLocalDateTime
    val to = LocalDateTime.of(endYear, endMonth,1,0,0, 0).atZone(ZoneOffset.UTC).toLocalDateTime

  }


}
