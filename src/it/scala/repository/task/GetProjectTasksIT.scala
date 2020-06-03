package repository.task

import java.time.{LocalDateTime, ZoneOffset, ZonedDateTime}
import java.util.UUID

import cats.effect.{ContextShift, IO}
import com.dimafeng.testcontainers.{ForAllTestContainer, PostgreSQLContainer}
import db.InitializeDatabase
import doobie.util.ExecutionContexts
import doobie.util.transactor.Transactor
import models.request.LogTaskRequest
import org.scalatest.{BeforeAndAfterEach, GivenWhenThen}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import repository.project.CreateProject
import repository.user.CreateUser

class GetProjectTasksIT extends AnyFlatSpec with Matchers with GivenWhenThen with ForAllTestContainer with BeforeAndAfterEach {

  override val container = new PostgreSQLContainer()

  it should "fetch project tasks" in new Context {

    Given("existing user")
    val userId = createUser(UUID.randomUUID().toString).unsafeRunSync().get

    And("existing project")
    val projectName = "test_project"
    val projectId = insertProject(projectName, userId).unsafeRunSync()

    And("existing tasks")
    val req1 = LogTaskRequest(projectName, "test description 1", ZonedDateTime.now(ZoneOffset.UTC), 50, None, None)
    val req2 = LogTaskRequest(projectName, "test description 2", ZonedDateTime.now(ZoneOffset.UTC).minusDays(5), 50, None, None)
    val task1 = insertTask(req1,projectId.right.get, userId, LocalDateTime.now()).unsafeRunSync()
    val task2 = insertTask(req2,projectId.right.get, userId, LocalDateTime.now()).unsafeRunSync()


    When("fetching project tasks")
    val result = getProjectTasks(projectId.right.get).unsafeRunSync()


    Then("it should return correct quantity of tasks")
    result.right.get.size shouldBe 2

  }

  private trait Context {

    implicit val cs: ContextShift[IO] = IO.contextShift(ExecutionContexts.synchronous)

    val tx = Transactor.fromDriverManager[IO](
      container.driverClassName,
      container.jdbcUrl,
      container.username,
      container.password
    )

    val insertProject = new CreateProject[IO](tx)
    val createUser = new CreateUser[IO](tx)
    val insertTask = new CreateTask[IO](tx)
    val getProjectTasks = new GetProjectTasks[IO](tx)

    import doobie.implicits._

    sql"DELETE from tb_project".update.run.transact(tx).unsafeRunSync()
    sql"DELETE from tb_user".update.run.transact(tx).unsafeRunSync()
    sql"DELETE from tb_task".update.run.transact(tx).unsafeRunSync()
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
