package service.user

import java.time._

import cats.effect._
import cats.implicits._
import errorMessages._
import models.model._
import models.request._
import org.scalatest.GivenWhenThen
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import repository.project.FindActiveProjectById
import repository.task._
import repository.user.{CreateUser, GetExistingUserId, UserById}
import service.task.TaskDelete

class UserCreateTest extends AnyFlatSpec with Matchers with GivenWhenThen {

  it should "create new user" in new Context {
    Given("user wants to delete task")
    val userId = 232
    val user = User(userId, "dsadas324hdsfjks")


    And("a service will find project id, user, delete(update) task for that data and return 1")
    val createUser = serviceUnderTest(Some(userId), Some(user))

    val deleteTaskRequest = DeleteTaskRequest(
      taskDescription = "Test task description",
      projectName = "test project name"
    )

    When("Deleting task")
    val result: Either[AppBusinessError, User] = createUser().unsafeRunSync()

    Then("returns number of rows updated")
    result shouldBe Right(user)
  }


  private trait Context {

    def serviceUnderTest(
                         userId: Option[Int],
                         createdUser: Option[User]
                        ): UserCreate[IO] = {

      val getNewUser = new UserById[IO](null) {
        override def apply(id: Int): IO[Option[User]] = createdUser.pure[IO]
      }
      val create = new CreateUser[IO](null) {
        override def apply(uuid: String): IO[Option[Int]] = userId.pure[IO]
      }

      new UserCreate[IO](getNewUser, create)
    }
  }


}
