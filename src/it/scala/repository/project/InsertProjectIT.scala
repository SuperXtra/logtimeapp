package repository.project

import java.util.UUID

import com.dimafeng.testcontainers.{ForAllTestContainer, PostgreSQLContainer}
import db.InitializeDatabase
import doobie.util.ExecutionContexts
import doobie.util.transactor.Transactor
import models.model.Project
import org.scalatest.{BeforeAndAfterEach, GivenWhenThen}
import pureconfig.ConfigSource
import repository.user.CreateUser
import java.time._

import pureconfig._
import cats.effect._
import cats.implicits._
import errorMessages.{AppBusinessError, ProjectNotCreated}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class InsertProjectIT extends AnyFlatSpec with Matchers with GivenWhenThen with ForAllTestContainer with BeforeAndAfterEach {

  override val container = new PostgreSQLContainer()

  it should "insert project" in new Context {

    Given("existing user")
    val userId = createUser(UUID.randomUUID().toString).unsafeRunSync().get

    And("existing project")
    val projectName = "test_project"
    val projectId = insertProject(projectName, userId).unsafeRunSync()

    And("a data access function able of finding active projects")
    val findActiveProjectByName = new FindProjectByName[IO](tx)

    When("fetching active project by name")
    val result = findActiveProjectByName(projectName).unsafeRunSync.right.get.id

    Then("it should return existing project")
    result shouldBe projectId.right.get
  }


  it should "not allow to insert project with existing name" in new Context {

    Given("existing user")
    val userId = createUser(UUID.randomUUID().toString).unsafeRunSync().get

    And("existing project")
    val projectName = "test_project"
    val projectId = insertProject(projectName, userId).unsafeRunSync()


    val result = insertProject(projectName, userId).unsafeRunSync()


    Then("it should return ProjectNotCreated")
    result shouldBe Left(ProjectNotCreated())
  }

  private trait Context {

    implicit val cs: ContextShift[IO] = IO.contextShift(ExecutionContexts.synchronous)

    val tx = Transactor.fromDriverManager[IO](
      container.driverClassName,
      container.jdbcUrl,
      container.username,
      container.password
    )

    val insertProject = new InsertProject(tx)
    val createUser = new CreateUser[IO](tx)

    import doobie.implicits._
    sql"DELETE from tb_project".update.run.transact(tx).unsafeRunSync()
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