package routes

import akka.http.scaladsl.model.ContentTypes.`application/json`
import akka.http.scaladsl.model.{HttpEntity, HttpResponse, StatusCodes}
import akka.http.scaladsl.server.{Directive1, Route}
import cats.effect.IO
import error.{ProjectNotFound, ReportCouldNotBeGenerated}
import java.time.{LocalDateTime, ZonedDateTime}

import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.http.scaladsl.unmarshalling.Unmarshal
import cats.implicits._
import io.circe.parser.{parse => json}
import models.{Active, ProjectId, TaskDuration, TaskId, UserId, WorkedTime}
import models.model.{Project, Task}
import models.reports.{FinalProjectReport, Tasks}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import service.auth.Auth

import scala.concurrent.duration._


class ReportRoutesTest extends AnyFlatSpec with Matchers with ScalatestRouteTest {

  it should "return project report" in new Context {
    val time = LocalDateTime.parse("2020-01-15T04:00:00")
    val project = Project(ProjectId(1), UserId(1), "project", time, None, Some(Active(true)))
    val result = FinalProjectReport(project, Tasks(List(Task(TaskId(1), ProjectId(1), UserId(1), time, "task", time, time, TaskDuration(20), None, None, None, Some(Active(true))))), WorkedTime(40))
    val route = Route.seal(ReportRoutes.projectTasksReport((_) => IO(result.asRight)))
    Get("/report/project?name=project") ~> route ~> check {
      response.status shouldBe StatusCodes.OK
      json(response.raw) shouldBe json(
        """
          |{
          |  "project" : {
          |    "id" : {
          |      "value" : 1
          |    },
          |    "userId" : {
          |      "value" : 1
          |    },
          |    "projectName" : "project",
          |    "createTime" : "2020-01-15T04:00:00",
          |    "deleteTime" : null,
          |    "active" : {
          |      "value" : true
          |    }
          |  },
          |  "tasks" : {
          |    "tasks" : [
          |      {
          |        "id" : {
          |          "value" : 1
          |        },
          |        "projectId" : {
          |          "value" : 1
          |        },
          |        "userId" : {
          |          "value" : 1
          |        },
          |        "createTime" : "2020-01-15T04:00:00",
          |        "taskDescription" : "task",
          |        "startTime" : "2020-01-15T04:00:00",
          |        "endTime" : "2020-01-15T04:00:00",
          |        "duration" : {
          |          "value" : 20
          |        },
          |        "volume" : null,
          |        "comment" : null,
          |        "deleteTime" : null,
          |        "active" : {
          |          "value" : true
          |        }
          |      }
          |    ]
          |  },
          |  "workedTimeInMinutes" : {
          |    "value" : 40
          |  }
          |}
        """.stripMargin
      )
    }
  }

  it should "not return project report" in new Context {

    val route = Route.seal(ReportRoutes.projectTasksReport((_) => IO(ReportCouldNotBeGenerated.asLeft)))
    Get("/report/project?name=project") ~> route ~> check {
      response.status shouldBe StatusCodes.OK
      json(response.raw) shouldBe json(
        """
          |{
          |  "error" : "error.report.not.generated"
          |}
        """.stripMargin
      )
    }
  }

  private trait Context {

    implicit class RawResponseOps(response: HttpResponse) {

      import org.scalatest.concurrent.ScalaFutures._

      def raw: String = Unmarshal(response.entity).to[String].futureValue
    }

    import akka.http.scaladsl.server.directives.BasicDirectives._

    implicit val auth: Auth = new Auth {
      def apply: Directive1[Map[String, Any]] = provide(Map("uuid" -> "test_token"))

      override def token(uuid: String): String = "test_token"
    }
  }

}
