package service.user

import akka.event.{MarkerLoggingAdapter, NoMarkerLogging}
import cats.effect.IO
import org.scalatest.GivenWhenThen
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import repository.user.UserExists
import cats.implicits._
import error.LogTimeAppError
import models.Exists
import models.model.User
import models.request.DeleteTaskRequest
import service.SetUp
import slick.dbio.{DBIOAction, Effect}
import slick.jdbc.PostgresProfile

class UserAuthResponseTest extends AnyFlatSpec with Matchers with GivenWhenThen {

  it should "confirm that user exists" in new Context {
    Given("user uuid to verify")
    val exists = Exists(true).asRight
    val userUUID = "dsadas324hdsfjks"

    And("a service will check if user exists")
    val checkIfUserExists = serviceUnderTest(exists)

    When("checking if user exists")
    val result = checkIfUserExists(userUUID).unsafeRunSync()

    Then("returns information that user exists")
    result shouldBe Exists(true).asRight
  }

  it should "confirm that user does not exists" in new Context {
    Given("user uuid to verify")
    val exists = Exists(false).asRight
    val userUUID = "dsadas324hdsfjks"

    And("a service will check if user exists")
    val checkIfUserExists = serviceUnderTest(exists)

    When("checking if user exists")
    val result = checkIfUserExists(userUUID).unsafeRunSync()

    Then("returns information that user does not exist")
    result shouldBe Exists(false).asRight
  }

  private trait Context extends SetUp {

    def serviceUnderTest(userExists: Either[LogTimeAppError, Exists]): AuthenticateUser[IO] = {

      val exists = new UserExists[IO] {
        override def apply(uuid: String) = DBIOAction.successful(userExists)
      }
      new AuthenticateUser[IO](exists)
    }
  }

}
