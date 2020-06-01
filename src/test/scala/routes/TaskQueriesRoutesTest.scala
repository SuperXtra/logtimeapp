package routes

import java.time.{LocalDateTime, ZoneOffset, ZonedDateTime}

import akka.http.scaladsl.model.ContentTypes._
import akka.http.scaladsl.model._
import akka.http.scaladsl.server._
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.http.scaladsl.unmarshalling.Unmarshal
import cats.effect._
import cats.implicits._
import errorMessages._
import io.circe.parser.{parse => json}
import models.model.Task
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import io.circe.syntax._
import service.auth.Auth

import scala.concurrent.duration._

class TaskQueriesRoutesTest extends AnyFlatSpec with Matchers with ScalatestRouteTest {

  it should "return error project not found" in new Context {
    val result = ProjectNotFound().asLeft
    val route = Route.seal(TaskRoutes.logTask((_,_) => IO(result)))
    Post("/task",
      HttpEntity(
        `application/json`,
        s"""
           |{
           |"projectName": "Project 23!",
           |"taskDescription": "this is task description",
           |"startTime": "${ZonedDateTime.now.minusDays(3)}",
           |"durationTime": ${3.days.toSeconds},
           |"volume": 8,
           |"comment": "this is comment"
           |}
           |""".stripMargin
      )
    ) ~> route ~> check {
      response.status shouldBe StatusCodes.NotFound
      json(response.raw) shouldBe json("""
        {
            "error" : "error.project.not.found"
        }
        """
      )
    }
  }

  it should "return created task" in new Context {

    val duration = 3.days
    val task = Task(
      id = 283,
      projectId = 213,
      userId = 123,
      taskDescription = "this is description",
      startTime = LocalDateTime.now().minusSeconds(3.days.toSeconds),
      endTime = LocalDateTime.now,
      duration = 3.days.toSeconds.toInt,
      createTime = ZonedDateTime.now(ZoneOffset.UTC).toLocalDateTime,
      volume = 2.some,
      comment = "this is comment".some,
      deleteTime = none,
      active = Some(false)
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
           |  "durationTime": ${duration.toSeconds},
           |  "volume": ${task.volume.get},
           |  "comment": "${task.comment.get}"
           |}
           |""".stripMargin
      )
    ) ~> route ~> check {
      response.status shouldBe StatusCodes.OK
      json(response.raw) shouldBe json(
        s"""
        {
          "id" : 283,
          "projectId" : 213,
          "userId" : 123,
          "createTime" : "${task.createTime}",
          "taskDescription" : "this is description",
          "startTime" : "${task.startTime}",
          "endTime" : "${task.endTime}",
          "duration" : ${task.duration},
          "volume" : 2,
          "comment" : "this is comment",
          "deleteTime" : null,
          "active" : false
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