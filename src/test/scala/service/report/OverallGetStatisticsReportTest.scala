package service.report

import cats.effect.IO
import error.LogTimeAppError
import models.request._
import models.reports.OverallStatisticsReport
import repository.report.GetDetailedReport
import cats.implicits._
import models.TotalCount
import org.scalatest.GivenWhenThen
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class OverallGetStatisticsReportTest extends AnyFlatSpec with Matchers with GivenWhenThen {

  it should "generate overall statistics report" in new Context {
    Given("user wants to delete task")
    val userStatisticsReport = OverallStatisticsReport(
      TotalCount(23),
      BigDecimal(23).some,
      None,
      None
    )

    And("a service will generate user statistics report")
    val report = serviceUnderTest(userStatisticsReport.asRight)

    val query = MainReport(None, None, None)

    When("creating report")
    val result = report(query).unsafeRunSync()

    Then("returns user statistics record")
    result shouldBe Right(userStatisticsReport)
  }

  private trait Context {
    def serviceUnderTest(
                          taskUpdateResult: Either[LogTimeAppError, OverallStatisticsReport]
                        ): GetStatisticsReport[IO] = {

      val getReport = new GetDetailedReport[IO](null) {
        override def apply(req: MainReport): IO[Either[LogTimeAppError, OverallStatisticsReport]] = taskUpdateResult.pure[IO]
      }

      new GetStatisticsReport[IO](getReport)
    }
  }
}
