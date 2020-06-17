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
import repository.project.InsertProject
import repository.user.InsertUser

class DeleteTasksIT extends AnyFlatSpec with Matchers with GivenWhenThen with ForAllTestContainer with BeforeAndAfterEach {

  override val container = new PostgreSQLContainer()

  it should "delete tasks" in new Context {

    Given("existing user")
    val userId = createUser(UUID.randomUUID().toString).unsafeRunSync().right.get

    And("existing project")
    val projectName = "test_project"
    val projectId = insertProject(projectName, userId).unsafeRunSync()

    And("existing tasks")
    val req1 = LogTaskRequest(projectName, "test description 1", ZonedDateTime.now(ZoneOffset.UTC), 50, None, None)
    val req2 = LogTaskRequest(projectName, "test description 2", ZonedDateTime.now(ZoneOffset.UTC).minusDays(5), 50, None, None)
    val task1 = insertTask(req1,projectId.right.get, userId, LocalDateTime.now()).unsafeRunSync()
    val task2 = insertTask(req2,projectId.right.get, userId, LocalDateTime.now()).unsafeRunSync()

    When("deleting inserted tasks")
    deleteTasks(projectId.right.get, LocalDateTime.now()).unsafeRunSync()

    And("fetching information about deleted tasks")
    val result = getTask(task1.right.get).unsafeRunSync().get.active
    val result2 = getTask(task2.right.get).unsafeRunSync().get.active

    Then("it should return both tasks are inactive")
    result shouldBe Some(false)
    result2 shouldBe Some(false)
  }

  private trait Context {

    implicit val cs: ContextShift[IO] = IO.contextShift(ExecutionContexts.synchronous)

    val tx = Transactor.fromDriverManager[IO](
      container.driverClassName,
      container.jdbcUrl,
      container.username,
      container.password
    )

    val getTask = new GetTask[IO](tx)
    val insertProject = new InsertProject[IO](tx)
    val createUser = new InsertUser[IO](tx)
    val insertTask = new CreateTask[IO](tx)
    val deleteTasks = new DeleteTasks[IO](tx)

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
