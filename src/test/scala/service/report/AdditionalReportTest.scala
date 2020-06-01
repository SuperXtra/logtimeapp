package service.report

import java.time.LocalDateTime

import cats.effect.IO
import errorMessages.AppBusinessError
import models.model.{Project, Task, TaskToUpdate}
import models.request.{DeleteTaskRequest, MainReport}
import models.responses.UserStatisticsReport
import repository.report.DetailedReport
import repository.task.{DeleteTask, GetUserTask, TaskInsertUpdate}
import repository.user.GetExistingUserId
import service.task.TaskUpdate
import cats.implicits._
import org.scalatest.GivenWhenThen
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class AdditionalReportTest extends AnyFlatSpec with Matchers with GivenWhenThen {

  it should "generate report" in new Context {
    Given("user wants to delete task")
    val userStatisticsReport = UserStatisticsReport(
      "asdd",
      23L,
      23,
      None,
      None
    )


    And("a service will generate user statistics report")
    val report = serviceUnderTest(List(userStatisticsReport).asRight)

    val query = MainReport(None, None, None)

    When("creating report")
    val result = report(query).unsafeRunSync()

    Then("returns user statistics record")
    result shouldBe Right(List(userStatisticsReport))
  }

  private trait Context {
    def serviceUnderTest(
                          taskUpdateResult: Either[AppBusinessError, List[UserStatisticsReport]]
                        ): AdditionalReport[IO] = {

      val getReport = new DetailedReport[IO](null) {
        override def apply(req: MainReport): IO[Either[AppBusinessError, List[UserStatisticsReport]]] = taskUpdateResult.pure[IO]
      }

      new AdditionalReport[IO](getReport)
    }
  }
}
