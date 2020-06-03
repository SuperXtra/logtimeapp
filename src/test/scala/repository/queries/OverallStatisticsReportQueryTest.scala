package repository.queries

import models.request.{DateFilter, MainReport}
import repository.query.StatisticsReportQuery

class OverallStatisticsReportQueryTest extends QueryTest {

  test("Check if query is working"){
    check(StatisticsReportQuery(MainReport(Some(List("asd", "qwert")), Some(DateFilter(2020, 1)), Some(DateFilter(2025, 1)))))
  }


}
