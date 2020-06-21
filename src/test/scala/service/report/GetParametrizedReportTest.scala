package service.report

import java.time.LocalDateTime

import akka.event.{MarkerLoggingAdapter, NoMarkerLogging}
import cats.effect.IO
import error.LogTimeAppError
import models.request._
import models.reports._
import repository.report.GetReport
import cats.implicits._
import models.{Page, Quantity, TaskDuration}
import org.scalatest.GivenWhenThen
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import service.SetUp
import slick.dbio.DBIOAction
import slick.jdbc.PostgresProfile

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

    val taskList = List(ReportTask(reportFromDb.task_create_time, reportFromDb.task_description, reportFromDb.start_time, reportFromDb.end_time, None, None, reportFromDb.comment))
    val response = FinalParametrizedReport(reportFromDb.project_name, reportFromDb.project_create_time, taskList)

    Then("returns generated report")
    result shouldBe Right(ArrayBuffer(response))
  }


  private trait Context extends SetUp {

    def serviceUnderTest(report: Either[LogTimeAppError, List[ReportFromDb]]): GetParametrizedReport[IO] = {

      val getReport = new GetReport[IO] {
        override def apply(projectQuery: ReportBodyWithParamsRequest) = DBIOAction.successful(report)
      }

      new GetParametrizedReport[IO](getReport)
    }
  }

}
