package repository.queries

import models.request.{ReportBodyWithParamsRequest, ReportParams, ReportRequest}
import repository.query.GenerateReportQueries

class GenerateFinalParametrizedReportQueryTest extends QueryTest {

  test("Final parametrized report test") {
    GenerateReportQueries(ReportBodyWithParamsRequest(ReportRequest(Some(List("1", "test")), None,None), ReportParams(None,None,None,page=1, quantity = 20)))
  }
}
