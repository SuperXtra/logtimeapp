package repository.project

import java.util.UUID

import com.dimafeng.testcontainers.{ForAllTestContainer, PostgreSQLContainer}
import db.InitializeDatabase
import doobie.util.ExecutionContexts
import org.scalatest._
import pureconfig.ConfigSource
import repository.user.InsertUser
import cats.effect._
import cats.implicits._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import error._
import slick.jdbc.PostgresProfile.api._
import db.RunDBIOAction._


class InsertProjectIT extends AnyFlatSpec with Matchers with GivenWhenThen with ForAllTestContainer with BeforeAndAfterEach {

  override val container = new PostgreSQLContainer()

  it should "insert project" in new Context {

    Given("existing user")
    val userId = createUser(UUID.randomUUID().toString).exec.unsafeRunSync().right.get

    And("existing project")
    val projectName = "test_project"
    val projectId = insertProject(projectName, userId).exec.unsafeRunSync()

    And("a data access function able of finding active projects")
    val findActiveProjectByName = new GetProjectByName[IO]

    When("fetching active project by name")
    val result = findActiveProjectByName(projectName).exec.unsafeRunSync.right.get.id

    Then("it should return existing project")
    result shouldBe projectId.right.get
  }

  it should "not allow to insert project with existing name" in new Context {

    Given("existing user")
    val userId = createUser(UUID.randomUUID().toString).exec.unsafeRunSync().right.get

    And("existing project")
    val projectName = "test_project"
    insertProject(projectName, userId).exec.unsafeRunSync()

    When("inserting project with the same name")
    val result = insertProject(projectName, userId).exec.unsafeRunSync()

    Then("it should return ProjectNotCreated")
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

    val insertProject = new InsertProject[IO]
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