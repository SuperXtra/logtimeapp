package service.user

import cats.effect.IO
import org.scalatest.GivenWhenThen
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import repository.user.UserExists
import cats.implicits._
import error.AppError
import models.model.User
import models.request.DeleteTaskRequest

class UserAuthenticateTest extends AnyFlatSpec with Matchers with GivenWhenThen {

  it should "confirm that user exists" in new Context {
    Given("user uuid to verify")
    val exists = true
    val userUUID = "dsadas324hdsfjks"


    And("a service will check if user exists and return true")
    val checkIfUserExists = serviceUnderTest(exists)



    When("checking if user exists")
    val result: Boolean = checkIfUserExists(userUUID).unsafeRunSync()

    Then("returns number of rows updated")
    result shouldBe true
  }

  it should "confirm that user does not exists" in new Context {
    Given("user uuid to verify")
    val exists = false
    val userUUID = "dsadas324hdsfjks"


    And("a service will check if user exists and return false")
    val checkIfUserExists = serviceUnderTest(exists)



    When("checking if user exists")
    val result: Boolean = checkIfUserExists(userUUID).unsafeRunSync()

    Then("returns number of rows updated")
    result shouldBe false
  }



  private trait Context {

    def serviceUnderTest(userExists: Boolean): UserAuthenticate[IO] = {

      val exists = new UserExists[IO](null) {
        override def apply(uuid: String): IO[Boolean] = userExists.pure[IO]
      }

      new UserAuthenticate[IO](exists)
    }
  }

}
