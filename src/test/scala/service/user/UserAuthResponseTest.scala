//package service.user
//
//import akka.event.{MarkerLoggingAdapter, NoMarkerLogging}
//import cats.effect.IO
//import org.scalatest.GivenWhenThen
//import org.scalatest.flatspec.AnyFlatSpec
//import org.scalatest.matchers.should.Matchers
//import repository.user.UserExists
//import cats.implicits._
//import error.LogTimeAppError
//import models.Exists
//import models.model.User
//import models.request.DeleteTaskRequest
//
//class UserAuthResponseTest extends AnyFlatSpec with Matchers with GivenWhenThen {
//
//  it should "confirm that user exists" in new Context {
//    Given("user uuid to verify")
//    val exists = Exists(true)
//    val userUUID = "dsadas324hdsfjks"
//
//    And("a service will check if user exists")
//    val checkIfUserExists = serviceUnderTest(exists)
//
//    When("checking if user exists")
//    val result: Exists = checkIfUserExists(userUUID).unsafeRunSync()
//
//    Then("returns information that user exists")
//    result shouldBe Exists(true)
//  }
//
//  it should "confirm that user does not exists" in new Context {
//    Given("user uuid to verify")
//    val exists = Exists(false)
//    val userUUID = "dsadas324hdsfjks"
//
//    And("a service will check if user exists")
//    val checkIfUserExists = serviceUnderTest(exists)
//
//    When("checking if user exists")
//    val result: Exists = checkIfUserExists(userUUID).unsafeRunSync()
//
//    Then("returns information that user does not exist")
//    result shouldBe Exists(false)
//  }
//
//  private trait Context {
//
//    implicit lazy val logger: MarkerLoggingAdapter = NoMarkerLogging
//
//    def serviceUnderTest(userExists: Exists): AuthenticateUser[IO] = {
//
//      val exists = new UserExists[IO](null) {
//        override def apply(uuid: String): IO[Exists] = userExists.pure[IO]
//      }
//      new AuthenticateUser[IO](exists)
//    }
//  }
//
//}
