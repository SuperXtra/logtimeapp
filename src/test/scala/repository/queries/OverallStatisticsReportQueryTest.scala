package repository.queries

import models.request.{DateFilter, MainReport}
import repository.query.StatisticsReportQuery

class OverallStatisticsReportQueryTest extends QueryTest {

  test("Statistics report query test"){
    check(StatisticsReportQuery(MainReport(Some(List("qwerty", "trewq")), Some(DateFilter(2020, 1)), Some(DateFilter(2025, 1)))))
  }
}
