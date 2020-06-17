package service.user

import cats.effect.IO
import org.scalatest.GivenWhenThen
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import repository.user.UserExists
import cats.implicits._
import error.LogTimeAppError
import models.model.User
import models.request.DeleteTaskRequest

class UserAuthResponseTest extends AnyFlatSpec with Matchers with GivenWhenThen {

  it should "confirm that user exists" in new Context {
    Given("user uuid to verify")
    val exists = true
    val userUUID = "dsadas324hdsfjks"

    And("a service will check if user exists")
    val checkIfUserExists = serviceUnderTest(exists)

    When("checking if user exists")
    val result: Boolean = checkIfUserExists(userUUID).unsafeRunSync()

    Then("returns information that user exists")
    result shouldBe true
  }

  it should "confirm that user does not exists" in new Context {
    Given("user uuid to verify")
    val exists = false
    val userUUID = "dsadas324hdsfjks"

    And("a service will check if user exists")
    val checkIfUserExists = serviceUnderTest(exists)

    When("checking if user exists")
    val result: Boolean = checkIfUserExists(userUUID).unsafeRunSync()

    Then("returns information that user does not exist")
    result shouldBe false
  }



  private trait Context {

    def serviceUnderTest(userExists: Boolean): AuthenticateUser[IO] = {

      val exists = new UserExists[IO](null) {
        override def apply(uuid: String): IO[Boolean] = userExists.pure[IO]
      }

      new AuthenticateUser[IO](exists)
    }
  }

}
