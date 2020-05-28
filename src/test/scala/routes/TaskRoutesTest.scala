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
import models.model.TaskTb
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import io.circe.syntax._


import scala.concurrent.duration._

class TaskRoutesTest extends AnyFlatSpec with Matchers with ScalatestRouteTest {

  it should "return error project not found" in new Context {
    val result = ProjectNotFound().asLeft
    val route =
      Route.seal(TaskRoutes.logTask(_ => IO(result)))
    Post("/task",
      HttpEntity(
        `application/json`,
        s"""
           |{
           |"projectName": "Project 23!",
           |"userIdentification": "38862bd8-ad69-4a21-b159-4f52a27edee6",
           |"taskDescription": "this is task description",
           |"startTime": "${ZonedDateTime.now.minusDays(3)}",
           |"durationTime": ${3.days.toSeconds},
           |"volume": 8,
           |"deleteTime" : null,
           |"comment": "this is comment"
           |}
           |""".stripMargin
      )
    ) ~> route ~> check {
      response.status shouldBe StatusCodes.OK
      json(response.raw) shouldBe ""
    }
  }

  it should "return created task" in new Context {

    val duration = 3.days
    val task = TaskTb(
      id = 283,
      projectId = 213,
      userId = 123,
      taskDescription = "this is description",
      startTime = LocalDateTime.now().minusSeconds(3.days.toSeconds),
      endTime = LocalDateTime.now,
      duration=3.days.toSeconds.toInt,
      createTime = ZonedDateTime.now(ZoneOffset.UTC).toLocalDateTime,
      volume = 2.some,
      comment = "this is comment".some,
      deleteTime = none,
      active = Some(false)
    )

    val route =
      Route.seal(TaskRoutes.logTask(_ => IO(task.asRight)))
    Post("/task",
      HttpEntity(
        `application/json`,
        s"""
           |{
           |"projectName": "Project 23!",
           |"userIdentification": "38862bd8-ad69-4a21-b159-4f52a27edee6",
           |"taskDescription": "${task.taskDescription}",
           |"startTime": "${task.startTime}",
           |"durationTime": ${duration.toSeconds},
           |"volume": ${task.volume.get},
           |"deleteTime" : null,
           |"comment": "${task.comment.get}"
           |}
           |""".stripMargin
      )
    ) ~> route ~> check {
      response.status shouldBe StatusCodes.OK
    }
  }

  private trait Context {
    implicit class RawResponseOps(response: HttpResponse) {
      import org.scalatest.concurrent.ScalaFutures._
      def raw: String = Unmarshal(response.entity).to[String].futureValue
    }
  }
}