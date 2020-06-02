package repository.project

import java.time.{LocalDateTime, ZoneOffset, ZonedDateTime}
import java.util.UUID

import cats.effect.{ContextShift, IO}
import com.dimafeng.testcontainers.{ForAllTestContainer, PostgreSQLContainer}
import db.InitializeDatabase
import doobie.util.ExecutionContexts
import doobie.util.transactor.Transactor
import errorMessages._
import models.request.LogTaskRequest
import org.scalatest.{BeforeAndAfterEach, GivenWhenThen}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import repository.task.{GetTask, InsertTask}
import repository.user.CreateUser

class DeleteProjectWithTasksIT extends AnyFlatSpec with Matchers with GivenWhenThen with ForAllTestContainer with BeforeAndAfterEach  {

  override val container = new PostgreSQLContainer()

  it should "delete project with its tasks" in new Context {

    Given("existing user")
    val userId = createUser(UUID.randomUUID().toString).unsafeRunSync().get

    And("existing project")
    val projectName = "test_project"
    val projectId = insertProject(projectName, userId).unsafeRunSync()

    And("creating tasks")

    val req1 = LogTaskRequest(projectName, "test description 1", ZonedDateTime.now(ZoneOffset.UTC), 50, None, None)
    val req2 = LogTaskRequest(projectName, "test description 2", ZonedDateTime.now(ZoneOffset.UTC).minusDays(5), 50, None, None)
    val task1 = createTask(req1,projectId.right.get, userId, LocalDateTime.now()).unsafeRunSync()
    val task2 = createTask(req2,projectId.right.get, userId, LocalDateTime.now()).unsafeRunSync()

    val time = LocalDateTime.now()

    And("delete project")
    delete(userId, projectName, projectId.right.get, time).unsafeRunSync()

    When("trying to fetch deleted project and tasks")
    val project = findProject(projectName).unsafeRunSync
    val firstTask = getTask(task1.right.get).unsafeRunSync
    val secondTask = getTask(task2.right.get).unsafeRunSync

    Then("it should return true and active  = false for tasks")
    project shouldBe Left(ProjectNotFound())
    firstTask.get.active shouldBe Some(false)
    secondTask.get.active shouldBe Some(false)
  }


  it should "delete project without tasks" in new Context {

    Given("existing user")
    val userId = createUser(UUID.randomUUID().toString).unsafeRunSync().get

    And("existing project")
    val projectName = "test_project"
    val projectId = insertProject(projectName, userId).unsafeRunSync()

    val time = LocalDateTime.now()

    And("delete project")
    delete(userId, projectName, projectId.right.get, time).unsafeRunSync()

    When("trying to fetch deleted project")
    val project = findProject(projectName).unsafeRunSync


    Then("it should return true")
    project shouldBe Left(ProjectNotFound())
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
    val createTask = new InsertTask[IO](tx)
    val delete = new DeleteProjectWithTasks[IO](tx)
    val findProject = new FindProjectByName[IO](tx)
    val getTask = new GetTask[IO](tx)


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
