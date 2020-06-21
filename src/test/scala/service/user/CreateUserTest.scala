package service.user

import java.time._

import akka.event.{MarkerLoggingAdapter, NoMarkerLogging}
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
import service.SetUp
import service.task.DeactivateTask
import slick.{dbio, jdbc}
import slick.dbio.{DBIOAction, Effect}
import slick.jdbc.PostgresProfile

class CreateUserTest extends AnyFlatSpec with Matchers with GivenWhenThen {

  it should "create new user" in new Context {
    Given("user wants to delete task")
    val user = User(UserId(232), "dsadas324hdsfjks")

    And("a service will create new user")
    val createUser = serviceUnderTest(user.asRight, user.userId.asRight)

    When("creating user")
    val result = createUser.apply.unsafeRunSync()

    Then("returns created user")
    result shouldBe Right(user)
  }


  private trait Context extends SetUp {

    def serviceUnderTest(
                          user: Either[LogTimeAppError, User],
                          createdUser: Either[LogTimeAppError,UserId]
                        ): CreateUser[IO] = {

      val getNewUser = new GetUserById[IO] {
        override def apply(id: UserId) = DBIOAction.successful(user)
      }
      val create = new InsertUser[IO] {
        override def apply(uuid: String) = DBIOAction.successful(createdUser)
      }

      new CreateUser[IO](getNewUser, create)
    }
  }
}
