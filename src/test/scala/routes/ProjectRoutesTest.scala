package routes

import java.time.LocalDateTime

import akka.http.scaladsl.model.ContentTypes.`application/json`
import akka.http.scaladsl.model.{HttpEntity, HttpResponse, StatusCodes}
import akka.http.scaladsl.server.{Directive1, Route}
import cats.effect.IO
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.http.scaladsl.unmarshalling.Unmarshal
import cats.implicits._
import errorMessages.{ProjectDeleteUnsuccessfulUserIsNotTheOwner, ProjectNotCreated, ProjectUpdateUnsuccessful}
import io.circe.parser.{parse => json}
import models.model.Project
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import service.auth.Auth

class ProjectRoutesTest extends AnyFlatSpec with Matchers with ScalatestRouteTest {

  it should "return created project id" in new Context {

    val route =
      Route.seal(ProjectRoutes.createProject((_, _) => IO(1.asRight)))
    Post("/project",
      HttpEntity(
        `application/json`,
        s"""
           |{
           |"projectName": "test create project"
           |}
           |""".stripMargin
      )
    ) ~> route ~> check {
      response.status shouldBe StatusCodes.OK
      json(response.raw) shouldBe json(
        s"""
          1
        """
      )
    }
  }

  it should "return return error message that project creation was unsuccessful - duplicate name" in new Context {

    val route =
      Route.seal(ProjectRoutes.createProject((_, _) => IO(ProjectNotCreated().asLeft)))
    Post("/project",
      HttpEntity(
        `application/json`,
        s"""
           |{
           |"projectName": "test create project"
           |}
           |""".stripMargin
      )
    ) ~> route ~> check {
      response.status shouldBe StatusCodes.OK
      json(response.raw) shouldBe json(
        s"""
          {
              "error" : "error.project.update.unsuccessful.given.name.exists"
          }
        """
      )
    }
  }

  it should "update project name and return updated one" in new Context {

    val project = Project(1,1,"new test project name", LocalDateTime.parse("2020-06-03T13:18:38.01865"), None, Some(true))


    val route =
      Route.seal(ProjectRoutes.updateProject((_, _) => IO(project.asRight)))
    Put("/project",
      HttpEntity(
        `application/json`,
        s"""
           |{
           |"oldProjectName": "test project name",
           |"projectName": "new test project name"
           |}
           |""".stripMargin
      )
    ) ~> route ~> check {
      response.status shouldBe StatusCodes.OK
      json(response.raw) shouldBe json(
        s"""
         {
            "id" : 1,
            "userId" : 1,
            "projectName" : "new test project name",
            "createTime" : "2020-06-03T13:18:38.01865",
            "deleteTime" : null,
            "active" : true
        }
        """
      )
    }
  }

  it should "not update project name and return proper error message" in new Context {

    val route =
      Route.seal(ProjectRoutes.updateProject((_, _) => IO(ProjectUpdateUnsuccessful().asLeft)))
    Put("/project",
      HttpEntity(
        `application/json`,
        s"""
           |{
           |"oldProjectName": "test project name",
           |"projectName": "new test project name"
           |}
           |""".stripMargin
      )
    ) ~> route ~> check {
      response.status shouldBe StatusCodes.OK
      json(response.raw) shouldBe json(
        s"""
                 {
                    "error" : "error.project.update.unsuccessful.no.content.updated"
                 }
        """
      )
    }
  }

  it should "delete project" in new Context {

    val route =
      Route.seal(ProjectRoutes.deleteProject((_, _) => IO(().asRight)))
    Delete("/project",
      HttpEntity(
        `application/json`,
        s"""
           |{
           |"projectName": "test delete project"
           |}
           |""".stripMargin
      )
    ) ~> route ~> check {
      response.status shouldBe StatusCodes.OK
      json(response.raw) shouldBe json(
        s"""
                 {}
        """
      )
    }
  }

  it should "not delete project and return corresponding error information" in new Context {

    val route =
      Route.seal(ProjectRoutes.deleteProject((_, _) => IO(ProjectDeleteUnsuccessfulUserIsNotTheOwner().asLeft)))
    Delete("/project",
      HttpEntity(
        `application/json`,
        s"""
           |{
           |"projectName": "test delete project"
           |}
           |""".stripMargin
      )
    ) ~> route ~> check {
      response.status shouldBe StatusCodes.OK
      json(response.raw) shouldBe json(
        s"""
                 {
                     "error" : "error.project.delete.unsuccessful.user.not.owner"
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
