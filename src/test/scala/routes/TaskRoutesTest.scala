package routes

import java.time.{LocalDateTime, ZoneOffset, ZonedDateTime}

import akka.http.scaladsl.model.ContentTypes._
import akka.http.scaladsl.model._
import akka.http.scaladsl.server._
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.http.scaladsl.unmarshalling.Unmarshal
import cats.effect._
import cats.implicits._
import error._
import io.circe.parser.{parse => json}
import models.model.Task
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import io.circe.syntax._
import models.{Active, DeleteCount, ProjectId, TaskDuration, TaskId, UserId, Volume}
import service.auth.Auth
import io.circe.generic.auto._
import models.request.LogTaskRequest

import scala.concurrent.duration._

class TaskRoutesTest extends AnyFlatSpec with Matchers with ScalatestRouteTest {

  it should "return error project not found" in new Context {

    val result = ProjectNotFound.asLeft
    val route = Route.seal(TaskRoutes.logTask((_, _) => IO(result)))
    Post("/task",
      HttpEntity(
        `application/json`,
        s"""
           |{
           |"projectName": "Project 23!",
           |"taskDescription": "this is task description",
           |"startTime": "${ZonedDateTime.now.minusDays(3)}",
           |"durationTime": {
           |  "value": ${3.days.toSeconds}
           |  },
           |"volume": {
           | "value": 8
           | },
           |"comment": "this is comment"
           |}
           |""".stripMargin
      )
    ) ~> route ~> check {
      response.status shouldBe StatusCodes.OK
      json(response.raw) shouldBe json(
        """
        {
            "error" : "error.task.not.created.project.not.found"
        }
        """
      )
    }
  }

  it should "return created task" in new Context {

    val duration = 3.days.toSeconds.toInt
    val task = Task(
      id = TaskId(283),
      projectId = ProjectId(213),
      userId = UserId(123),
      taskDescription = "this is description",
      startTime = LocalDateTime.parse("2020-05-31T12:23:02"),
      duration = TaskDuration(3.days.toSeconds.toInt),
      endTime = LocalDateTime.parse("2020-05-31T12:23:02").plusDays(3),
      createTime = ZonedDateTime.now(ZoneOffset.UTC).toLocalDateTime,
      volume = Volume(2).some,
      comment = "this is comment".some,
      deleteTime = none,
      active = Some(Active(false))
    )
    val route =
      Route.seal(TaskRoutes.logTask((_, _) => IO(task.asRight)))
    Post("/task",
      HttpEntity(
        `application/json`,
                s"""
                   |{
                   |  "projectName": "Project 23!",
                   |  "taskDescription": "${task.taskDescription}",
                   |  "startTime": "${ZonedDateTime.of(task.startTime, ZoneOffset.UTC)}",
                   |  "durationTime": {
                   |      "value": ${duration}
                   |      },
                   |  "volume": {
                   |      "value": ${task.volume.get.value}
                   |      },
                   |  "comment": "${task.comment.get}"
                   |}
                   |""".stripMargin
      )
    ) ~> route ~> check {
      response.status shouldBe StatusCodes.OK
      json(response.raw) shouldBe json(
        s"""
    {
  "id" : {
    "value" : 283
  },
  "projectId" : {
    "value" : 213
  },
  "userId" : {
    "value" : 123
  },
  "createTime" : "${task.createTime.toString}",
  "taskDescription" : "this is description",
  "startTime" : "2020-05-31T12:23:02",
  "endTime" : "2020-06-03T12:23:02",
  "duration" : {
    "value" : 259200
  },
  "volume" : {
    "value" : 2
  },
  "comment" : "this is comment",
  "deleteTime" : null,
  "active" : {
    "value" : false
  }
}
        """
      )
    }
  }

  it should "update task" in new Context {
    val result = ().asRight
    val route = Route.seal(TaskRoutes.updateTask((_, _) => IO(result)))
    Put("/task",
      HttpEntity(
        `application/json`,
        s"""
           |{
           |	  "oldTaskDescription": "old task description",
           |    "newTaskDescription": "new task description",
           |    "startTime": "2020-03-10T14:00:00+02:00",
           |    "durationTime": {
           |      "value": 500
           |      },
           |    "volume": {
           |      "value": 3
           |      },
           |    "comment": "some interesting comment2"
           |}
           |""".stripMargin
      )
    ) ~> route ~> check {
      response.status shouldBe StatusCodes.OK
      json(response.raw) shouldBe json(
        """
        {}
        """
      )
    }
  }

  it should "not update task" in new Context {
    val result = TaskUpdateUnsuccessful.asLeft
    val route = Route.seal(TaskRoutes.updateTask((_, _) => IO(result)))
    Put("/task",
      HttpEntity(
        `application/json`,
        s"""
           |{
           |	  "oldTaskDescription": "old task description",
           |    "newTaskDescription": "new task description",
           |    "startTime": "2020-03-10T14:00:00+02:00",
           |    "durationTime": {
           |        "value": 500
           |        },
           |    "volume":  {
           |        "value": 3
           |        },
           |    "comment": "some interesting comment2"
           |}
           |""".stripMargin
      )
    ) ~> route ~> check {
      response.status shouldBe StatusCodes.OK
      json(response.raw) shouldBe json(
        """
        {
           "error" : "error.task.not.updated"
        }
        """
      )
    }
  }


  it should "delete task" in new Context {
    val result = DeleteCount(1).asRight
    val route = Route.seal(TaskRoutes.deleteTask((_, _, _) => IO(result)))
    Delete("/task",
      HttpEntity(
        `application/json`,
        s"""
           |{
           |	"taskDescription": "test 12345",
           |	"projectName": "test 5"
           |}
           |""".stripMargin
      )
    ) ~> route ~> check {
      response.status shouldBe StatusCodes.OK
      json(response.raw) shouldBe json(
        """
        {
           "value" : 1
        }
        """
      )
    }
  }

  it should "not delete task" in new Context {
    val result = TaskDeleteUnsuccessful.asLeft
    val route = Route.seal(TaskRoutes.deleteTask((_, _, _) => IO(result)))
    Delete("/task",
      HttpEntity(
        `application/json`,
        s"""
           |{
           |	"taskDescription": "test 12345",
           |	"projectName": "test 5"
           |}
           |""".stripMargin
      )
    ) ~> route ~> check {
      response.status shouldBe StatusCodes.OK
      json(response.raw) shouldBe json(
        """
        {
             "error" : "error.task.not.resolved"
        }
        """
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