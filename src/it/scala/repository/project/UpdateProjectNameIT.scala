package repository.project

import java.util.UUID

import cats.effect.{ContextShift, IO}
import com.dimafeng.testcontainers.{ForAllTestContainer, PostgreSQLContainer}
import db.InitializeDatabase
import doobie.util.ExecutionContexts
import doobie.util.transactor.Transactor
import errorMessages.{ProjectNotCreated, ProjectUpdateUnsuccessful}
import org.scalatest.{BeforeAndAfterEach, GivenWhenThen}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import repository.user.CreateUser

class UpdateProjectNameIT extends AnyFlatSpec with Matchers with GivenWhenThen with ForAllTestContainer with BeforeAndAfterEach {

  override val container = new PostgreSQLContainer()

  it should "update existing project id" in new Context {

    Given("existing user")
    val userId = createUser(UUID.randomUUID().toString).unsafeRunSync().get

    And("existing project")
    val projectName = "test_project"
    insertProject(projectName, userId).unsafeRunSync()

    And("a data access function able of finding active projects")
    val findActiveProjectByName = new FindProjectByName[IO](tx)

    And("updating existing project")
    val newName = projectName + "test"
    update(projectName, newName, userId).unsafeRunSync()

    When("fetching active project by name")
    val result = findActiveProjectByName(newName).unsafeRunSync.right.get.projectName

    Then("it should return existing project")
    result shouldBe newName
  }

  it should "not update existing project id" in new Context {

    Given("existing user")
    val userId = createUser(UUID.randomUUID().toString).unsafeRunSync().get

    And("existing project")
    val projectName = "test_project"
    val projectName2 = "test_project2"
    insertProject(projectName, userId).unsafeRunSync()
    insertProject(projectName2, userId).unsafeRunSync()

    And("a data access function able of finding active projects")
    val findActiveProjectByName = new FindProjectByName[IO](tx)

    When("updating existing project")
    val newName = projectName + "2"
    val result =     update(projectName, projectName2, userId).unsafeRunSync()

    Then("it should return existing project")
    result shouldBe ProjectUpdateUnsuccessful()
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
    val update = new UpdateProjectName[IO](tx)

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