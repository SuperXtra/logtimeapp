package repository.user

import java.util.UUID

import cats.effect.{ContextShift, IO}
import com.dimafeng.testcontainers.{ForAllTestContainer, PostgreSQLContainer}
import db.InitializeDatabase
import doobie.util.ExecutionContexts
import doobie.util.transactor.Transactor
import models.UserId
import models.model.User
import org.scalatest.{BeforeAndAfterEach, GivenWhenThen}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class GetUserByIdIT extends AnyFlatSpec with Matchers with GivenWhenThen with ForAllTestContainer with BeforeAndAfterEach {

  override val container = new PostgreSQLContainer()

  it should "find user by provided id" in new Context {

    Given("existing user")
    val uuid = UUID.randomUUID().toString
    val userId = createUser(uuid).unsafeRunSync().right.get

    And("a data access function able of finding user by id")
    val findUserById = new GetUserById[IO](tx)

    When("fetching user by id")
    val result = findUserById(userId).unsafeRunSync

    Then("it should return existing user")
    result shouldBe Some(User(userId, uuid))
  }

  it should "not find user by provided, non existing id" in new Context {

    Given("existing user")
    val userId = UserId(123)

    And("a data access function able of finding user by id")
    val findUserById = new GetUserById[IO](tx)

    When("fetching user by id")
    val result = findUserById(userId).unsafeRunSync

    Then("it should return none")
    result shouldBe None
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

