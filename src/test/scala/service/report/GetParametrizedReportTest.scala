package service.report

import java.time.LocalDateTime

import cats.effect.IO
import error.LogTimeAppError
import models.request._
import models.reports._
import repository.report.GetReport
import cats.implicits._
import models.{Page, Quantity}
import org.scalatest.GivenWhenThen
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.collection.mutable.ArrayBuffer

class GetParametrizedReportTest extends AnyFlatSpec with Matchers with GivenWhenThen {

  it should "generate parametrized report" in new Context {
    Given("report")
    val reportFromDb = ReportFromDb(
      project_name = Some("Report"),
      project_create_time = Some(LocalDateTime.now()),
      task_create_time = Some(LocalDateTime.now()),
      task_description = Some("Task description"),
      start_time = None,
      end_time = None,
      duration = None,
      volume = None,
      comment = None
    )

    And("a service will generate report")
    val report = serviceUnderTest(List(reportFromDb).asRight)

    And("parameters query")
    val query = ReportBodyWithParamsRequest(
      ReportRequest(None, None, None),
      ReportParams(None, None, None, Page(1), Quantity(10))
    )

    When("generating report")
    val result = report(query).unsafeRunSync()

    val taskList = List(ReportTask(reportFromDb.task_create_time, reportFromDb.task_description, reportFromDb.start_time, reportFromDb.end_time, reportFromDb.duration, reportFromDb.volume, reportFromDb.comment))
    val response = FinalParametrizedReport(reportFromDb.project_name, reportFromDb.project_create_time, taskList)

    Then("returns generated report")
    result shouldBe Right(ArrayBuffer(response))
  }


  private trait Context {
    def serviceUnderTest(report: Either[LogTimeAppError, List[ReportFromDb]]): GetParametrizedReport[IO] = {


      val getReport = new GetReport[IO](null) {
        override def apply(projectQuery: ReportBodyWithParamsRequest): IO[Either[LogTimeAppError, List[ReportFromDb]]] = report.pure[IO]
      }

      new GetParametrizedReport[IO](getReport)
    }
  }

}
