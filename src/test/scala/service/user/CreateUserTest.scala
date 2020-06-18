package service.user

import java.time._

import cats.effect._
import cats.implicits._
import error._
import models.UserId
import models.model._
import models.request._
import org.scalatest.GivenWhenThen
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import repository.task._
import repository.user.{GetUserById, GetUserByUUID, InsertUser}
import service.task.DeactivateTask

class CreateUserTest extends AnyFlatSpec with Matchers with GivenWhenThen {

  it should "create new user" in new Context {
    Given("user wants to delete task")
    val userId = UserId(232)
    val user = User(userId, "dsadas324hdsfjks")

    And("a service will create new user")
    val createUser = serviceUnderTest(userId.asRight, Some(user))

    When("creating user")
    val result = createUser.apply.unsafeRunSync()

    Then("returns created user")
    result shouldBe Right(user)
  }


  private trait Context {

    def serviceUnderTest(
                          userId: Either[CannotCreateUserWithGeneratedUUID.type, UserId],
                          createdUser: Option[User]
                        ): CreateUser[IO] = {

      val getNewUser = new GetUserById[IO](null) {
        override def apply(id: UserId): IO[Option[User]] = createdUser.pure[IO]
      }
      val create = new InsertUser[IO](null) {
        override def apply(uuid: String): IO[Either[CannotCreateUserWithGeneratedUUID.type, UserId]] = userId.pure[IO]
      }

      new CreateUser[IO](getNewUser, create)
    }
  }

}
