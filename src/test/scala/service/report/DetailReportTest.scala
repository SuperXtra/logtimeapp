package service.report

import java.time.LocalDateTime

import cats.effect.IO
import error.AppError
import models.request._
import models.responses._
import repository.report.Report
import cats.implicits._
import org.scalatest.GivenWhenThen
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class DetailReportTest extends AnyFlatSpec with Matchers with GivenWhenThen {

  it should "delete task" in new Context {
    Given("user wants to delete task")
    val reportFromDb = ReportFromDb(
      Some("Report"),
      Some(LocalDateTime.now()),
      Some(LocalDateTime.now()),
      Some("Task description"),
      None,
      None,
      None,
      None,
      None
    )


    And("a service will find project id, user, delete(update) task for that data and return 1")
    val report = serviceUnderTest(List(reportFromDb).asRight)

    val query = ReportBodyWithParamsRequest(
      ReportRequest(None,None, None),
      ReportParams(None,None, None)
    )

    When("Deleting task")
    val result = report(query).unsafeRunSync()

    Then("returns number of rows updated")
    result shouldBe Right(List(reportFromDb))
  }




  private trait Context {
    def serviceUnderTest(report: Either[AppError, List[ReportFromDb]]): DetailReport[IO] = {


      val getReport = new Report[IO](null){
        override def apply(projectQuery: ReportBodyWithParamsRequest): IO[Either[AppError, List[ReportFromDb]]] = report.pure[IO]
      }

      new DetailReport[IO](getReport)
    }
  }
}
