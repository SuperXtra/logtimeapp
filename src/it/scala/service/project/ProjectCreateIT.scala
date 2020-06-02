package service.project

import java.util.UUID

import cats.effect.{ContextShift, IO}
import cats.implicits.none
import com.dimafeng.testcontainers.{ForAllTestContainer, PostgreSQLContainer}
import db.InitializeDatabase
import doobie.util.ExecutionContexts
import doobie.util.transactor.Transactor
import models.model.Project
import org.scalatest.{BeforeAndAfterEach, GivenWhenThen}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import repository.project.{FindProjectByName, InsertProject}
import repository.user.{CreateUser, GetExistingUserId}
import cats.implicits._
import errorMessages.AppBusinessError
import models.request.CreateProjectRequest

class ProjectCreateIT extends AnyFlatSpec with Matchers with GivenWhenThen with ForAllTestContainer with BeforeAndAfterEach {

  override val container = new PostgreSQLContainer()

  it should "create new project" in new Context {

    Given("existing user")
    val uuid = UUID.randomUUID().toString
    val userId = createUser(uuid).unsafeRunSync().get


    And("creating new project")
    val projectName = "test_project"
    val projectId = projectCreate(CreateProjectRequest(projectName), uuid).unsafeRunSync

    And("a data access function able of finding active projects")
    val findActiveProjectByName = new FindProjectByName[IO](tx)

    When("fetching active project by name")
    val result = findActiveProjectByName(projectName).unsafeRunSync.right.get.id

    Then("it should return existing project")
    Right(result) shouldBe projectId
  }

  private trait Context {

    implicit val cs: ContextShift[IO] = IO.contextShift(ExecutionContexts.synchronous)

    val tx = Transactor.fromDriverManager[IO](
      container.driverClassName,
      container.jdbcUrl,
      container.username,
      container.password
    )

    val createUser = new CreateUser[IO](tx)

    val getExistingUserId = new GetExistingUserId[IO](tx)
    val insertProject = new InsertProject[IO](tx)

    val projectCreate = new ProjectCreate[IO](getExistingUserId, insertProject)


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

