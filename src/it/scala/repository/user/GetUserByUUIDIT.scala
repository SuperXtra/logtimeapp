package repository.user

import java.util.UUID

import cats.effect.{ContextShift, IO}
import com.dimafeng.testcontainers.{ForAllTestContainer, PostgreSQLContainer}
import db.InitializeDatabase
import doobie.util.ExecutionContexts
import doobie.util.transactor.Transactor
import error.{ProjectNotCreated, UserNotFound}
import org.scalatest.{BeforeAndAfterEach, GivenWhenThen}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import slick.jdbc.PostgresProfile.api._
import db.RunDBIOAction._

class GetUserByUUIDIT extends AnyFlatSpec with Matchers with GivenWhenThen with ForAllTestContainer with BeforeAndAfterEach {

  override val container = new PostgreSQLContainer()

  it should "return existing user id" in new Context {

    Given("existing user")
    val uuid = UUID.randomUUID().toString
    val userId = createUser(uuid).exec.unsafeRunSync().right.get

    And("a data access function able of finding user id by uuid")
    val getUserId = new GetUserByUUID[IO]

    When("fetching user id by uuid")
    val result = getUserId(uuid).exec.unsafeRunSync()

    Then("it should return existing user id")
    result.right.get.userId shouldBe userId
  }

  it should "return error when trying to return not existing user id" in new Context {

    Given("existing user")
    val uuid = UUID.randomUUID().toString

    And("a data access function able of finding user id by uuid")
    val getUserId = new GetUserByUUID[IO]

    When("fetching existing user id")
    val result = getUserId(uuid).exec.unsafeRunSync()

    Then("it should return none")
    result shouldBe Left(ProjectNotCreated)
  }

  private trait Context {

    implicit val cs: ContextShift[IO] = IO.contextShift(ExecutionContexts.synchronous)

    implicit val tx: Database = Database.forURL(
      container.jdbcUrl,
      container.username,
      container.password,
      null,
      container.driverClassName
    )

    val createUser = new InsertUser[IO]

    for {
      _ <- sql"DELETE from tb_project".asUpdate.exec
      _ <- sql"DELETE from tb_user".asUpdate.exec
      _ <- sql"DELETE from tb_task".asUpdate.exec
    } yield ()
  }

  override def beforeEach(): Unit = {
    container.start()
    new InitializeDatabase[IO].apply(
      container.jdbcUrl,
      container.username,
      container.password
    ).unsafeRunSync()
  }
}
