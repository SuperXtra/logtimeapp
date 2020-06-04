package routes

import akka.http.scaladsl.model.ContentTypes.`application/json`
import akka.http.scaladsl.model.{HttpEntity, HttpResponse, StatusCodes}
import akka.http.scaladsl.server.{Directive1, Route}
import cats.effect.IO
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.http.scaladsl.unmarshalling.Unmarshal
import cats.implicits._
import error.{AuthenticationNotSuccessful, CannotCreateUserWithGeneratedUUID}
import io.circe.parser.{parse => json}
import models.model.User
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import service.auth.Auth

class UserRoutesTest  extends AnyFlatSpec with Matchers with ScalatestRouteTest {

  it should "should create user" in new Context {

    val user = User(21, "61a3a602-3797-41c2-8d69-764f762f1484")
    val route =
      Route.seal(UserRoutes.createUser(IO(user.asRight)))
    Post("/user/register") ~> route ~> check {
      response.status shouldBe StatusCodes.OK
      json(response.raw) shouldBe json(
        s"""
          {
             "id": 21,
             "userIdentification": "61a3a602-3797-41c2-8d69-764f762f1484"
          }
        """
      )
    }
  }

  it should "should not create user due to uuid conflict" in new Context {
    val route =
      Route.seal(UserRoutes.createUser(IO(CannotCreateUserWithGeneratedUUID.asLeft)))
    Post("/user/register") ~> route ~> check {
      response.status shouldBe StatusCodes.OK
      json(response.raw) shouldBe json(
        s"""
          {
            "error" : "error.user.not.created.duplicate.uuid"
          }
        """
      )
    }
  }

  it should "should authenticate user" in new Context {
    val route =
      Route.seal(UserRoutes.authorizeUser(_ => IO(true)))
    Post("/user/login",
      HttpEntity(
        `application/json`,
        s"""
           |{
           |    "userUUID": "5425dc80-5f34-4424-b5de-ee2b1f00f2a5"
           |}
           |""".stripMargin
      )) ~> route ~> check {
      response.status shouldBe StatusCodes.OK
      json(response.raw) shouldBe json(
        s"""
            {
                "token" : "test_token"
            }
        """
      )
    }
  }

  it should "should not authenticate user" in new Context {

    val route =
      Route.seal(UserRoutes.authorizeUser(_ => IO(false)))
    Post("/user/login",
      HttpEntity(
        `application/json`,
        s"""
           |{
           |    "userUUID": "5425dc80-5f34-4424-b5de-ee2b1f00f2a5"
           |}
           |""".stripMargin
      )) ~> route ~> check {
      response.status shouldBe StatusCodes.Unauthorized
      json(response.raw) shouldBe json(
        s"""
                {
                  "error" : "error.authentication.not.successful"
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
