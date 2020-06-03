package service.report

import java.time.LocalDateTime

import cats.effect.IO
import errorMessages.AppBusinessError
import models.model.{Project, Task, TaskToUpdate}
import models.request.{DeleteTaskRequest, MainReport}
import models.responses.OverallStatisticsReport
import repository.report.DetailedReport
import repository.task.{DeleteTask, GetUserTask, UpdateTask}
import repository.user.GetUserId
import service.task.TaskUpdate
import cats.implicits._
import org.scalatest.GivenWhenThen
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class OverallStatisticsReportTest extends AnyFlatSpec with Matchers with GivenWhenThen {

  it should "generate report" in new Context {
    Given("user wants to delete task")
    val userStatisticsReport = OverallStatisticsReport(
      23L,
      23,
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
                          taskUpdateResult: Either[AppBusinessError, OverallStatisticsReport]
                        ): StatisticsReport[IO] = {

      val getReport = new DetailedReport[IO](null) {
        override def apply(req: MainReport): IO[Either[AppBusinessError, OverallStatisticsReport]] = taskUpdateResult.pure[IO]
      }

      new StatisticsReport[IO](getReport)
    }
  }
}
