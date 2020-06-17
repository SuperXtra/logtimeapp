package repository.user

import java.util.UUID

import cats.effect.{ContextShift, IO}
import com.dimafeng.testcontainers.{ForAllTestContainer, PostgreSQLContainer}
import db.InitializeDatabase
import doobie.util.ExecutionContexts
import doobie.util.transactor.Transactor
import models.model.User
import org.scalatest.{BeforeAndAfterEach, GivenWhenThen}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class UserExistsIT extends AnyFlatSpec with Matchers with GivenWhenThen with ForAllTestContainer with BeforeAndAfterEach {

  override val container = new PostgreSQLContainer()

  it should "find existing user" in new Context {

    Given("existing user")
    val uuid = UUID.randomUUID().toString
    createUser(uuid).unsafeRunSync().right.get

    And("a data access function able of determining whether user exists or not")
    val exists = new UserExists[IO](tx)

    When("checking whether uuid exists")
    val result = exists(uuid).unsafeRunSync

    Then("it should return that user exists")
    result shouldBe true
  }

  it should "not find existing user" in new Context {

    Given("not existing user uuid")
    val uuid = UUID.randomUUID().toString


    And("a data access function able of determining whether user exists or not")
    val exists = new UserExists[IO](tx)

    When("checking whether uuid exists")
    val result = exists(uuid).unsafeRunSync

    Then("it should return that user does not exist")
    result shouldBe false
  }

  private trait Context {

    implicit val cs: ContextShift[IO] = IO.contextShift(ExecutionContexts.synchronous)

    val tx = Transactor.fromDriverManager[IO](
      container.driverClassName,
      container.jdbcUrl,
      container.username,
      container.password
    )

    val createUser = new InsertUser[IO](tx)

    import doobie.implicits._

    (for {
      _ <- sql"DELETE from tb_project".update.run
      _ <- sql"DELETE from tb_user".update.run
      _ <- sql"DELETE from tb_task".update.run
    } yield ()).transact(tx).unsafeRunSync()
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

