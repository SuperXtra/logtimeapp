package repository.user

import java.util.UUID

import cats.effect.{ContextShift, IO}
import com.dimafeng.testcontainers.{ForAllTestContainer, PostgreSQLContainer}
import db.InitializeDatabase
import doobie.util.ExecutionContexts
import doobie.util.transactor.Transactor
import models.Exists
import models.model.User
import org.scalatest.{BeforeAndAfterEach, GivenWhenThen}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import slick.jdbc.PostgresProfile.api._
import db.RunDBIOAction._

class UserExistsIT extends AnyFlatSpec with Matchers with GivenWhenThen with ForAllTestContainer with BeforeAndAfterEach {

  override val container = new PostgreSQLContainer()

  it should "find existing user" in new Context {

    Given("existing user")
    val uuid = UUID.randomUUID().toString
    createUser(uuid).exec.unsafeRunSync().right.get

    And("a data access function able of determining whether user exists or not")
    val exists = new UserExists[IO]

    When("checking whether uuid exists")
    val result = exists(uuid).exec.unsafeRunSync()

    Then("it should return that user exists")
    result shouldBe Right(Exists(true))
  }

  it should "not find existing user" in new Context {

    Given("not existing user uuid")
    val uuid = UUID.randomUUID().toString


    And("a data access function able of determining whether user exists or not")
    val exists = new UserExists[IO]

    When("checking whether uuid exists")
    val result = exists(uuid).exec.unsafeRunSync()

    Then("it should return that user does not exist")
    result shouldBe Right(Exists(false))
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