package repository.task

import java.time.{LocalDateTime, ZoneOffset, ZonedDateTime}
import java.util.UUID

import cats.effect.{ContextShift, IO}
import com.dimafeng.testcontainers.{ForAllTestContainer, PostgreSQLContainer}
import db.InitializeDatabase
import doobie.util.ExecutionContexts
import doobie.util.transactor.Transactor
import error.{LogTimeAppError, ProjectDeleteUnsuccessfulUserIsNotTheOwner}
import models.{Active, TaskDuration}
import models.request.LogTaskRequest
import org.scalatest.{BeforeAndAfterEach, GivenWhenThen}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import repository.project.{InsertProject, IsProjectOwner}
import repository.user.InsertUser

class DeleteTaskIT extends AnyFlatSpec with Matchers with GivenWhenThen with ForAllTestContainer with BeforeAndAfterEach {

  override val container = new PostgreSQLContainer()

  it should "delete task" in new Context {

    Given("existing user")
    val userId = createUser(UUID.randomUUID().toString).unsafeRunSync().right.get

    And("existing project")
    val projectName = "test_project"
    val projectId = insertProject(projectName, userId).unsafeRunSync()

    And("existing task")
    val req1 = LogTaskRequest(projectName, "test description 1", ZonedDateTime.now(ZoneOffset.UTC), TaskDuration(50), None, None)
    val task = insertTask(req1,projectId.right.get, userId, LocalDateTime.now()).unsafeRunSync()

    When("deleting inserted task")
    deleteTask("test description 1", projectId.right.get, userId, LocalDateTime.now()).unsafeRunSync()

    And("fetching information about deleted task")
    val result: Option[Active] = getTask(task.right.get).unsafeRunSync().get.active

    Then("it should return that task is inactive")
    result shouldBe Some(Active(false))
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
    val deleteTask = new DeleteTask[IO](tx)

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
