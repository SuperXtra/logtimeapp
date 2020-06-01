package service.report

import java.time.LocalDateTime

import cats.effect.IO
import errorMessages.AppBusinessError
import models.request._
import models.responses._
import repository.report.Report
import cats.implicits._
import org.scalatest.GivenWhenThen
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.collection.mutable.ArrayBuffer

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

    val taskList = List(ReportTask(reportFromDb.task_create_time, reportFromDb.task_description, reportFromDb.start_time, reportFromDb.end_time, reportFromDb.duration, reportFromDb.volume, reportFromDb.comment))
    val response = DetailReportResponse(reportFromDb.project_name, reportFromDb.project_create_time, taskList)

    Then("returns number of rows updated")
    result shouldBe Right(ArrayBuffer(response))
  }




  private trait Context {
    def serviceUnderTest(report: Either[AppBusinessError, List[ReportFromDb]]): DetailReport[IO] = {


      val getReport = new Report[IO](null){
        override def apply(projectQuery: ReportBodyWithParamsRequest): IO[Either[AppBusinessError, List[ReportFromDb]]] = report.pure[IO]
      }

      new DetailReport[IO](getReport)
    }
  }
}
