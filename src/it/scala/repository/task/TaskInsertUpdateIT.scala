package repository.task

import java.time.{LocalDateTime, ZoneOffset, ZonedDateTime}
import java.util.UUID

import cats.effect.{ContextShift, IO}
import com.dimafeng.testcontainers.{ForAllTestContainer, PostgreSQLContainer}
import db.InitializeDatabase
import doobie.util.ExecutionContexts
import doobie.util.transactor.Transactor
import models.model.TaskToUpdate
import models.request.LogTaskRequest
import org.scalatest.{BeforeAndAfterEach, GivenWhenThen}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import repository.project.InsertProject
import repository.user.CreateUser

class TaskInsertUpdateIT extends AnyFlatSpec with Matchers with GivenWhenThen with ForAllTestContainer with BeforeAndAfterEach {

  override val container = new PostgreSQLContainer()

  it should "update task" in new Context {

    Given("existing user")
    val userId = createUser(UUID.randomUUID().toString).unsafeRunSync().get

    And("existing project")
    val projectName = "test_project"
    val projectId = insertProject(projectName, userId).unsafeRunSync()

    And("existing task")
    val req1 = LogTaskRequest(projectName, "test description 1", ZonedDateTime.now(ZoneOffset.UTC), 50, None, None)
    val task1 = insertTask(req1,projectId.right.get, userId, LocalDateTime.now()).unsafeRunSync()


    When("updating task")
    val request = TaskToUpdate(projectId.right.get, userId, "test description 2", ZonedDateTime.now(ZoneOffset.UTC), 29L, None, None)
    val result = updateTask(request, LocalDateTime.now(), "test description 1", projectId.right.get, userId).unsafeRunSync()


    Then("it should return count of updated records")
    result shouldBe Right(())

  }

  private trait Context {

    implicit val cs: ContextShift[IO] = IO.contextShift(ExecutionContexts.synchronous)

    val tx = Transactor.fromDriverManager[IO](
      container.driverClassName,
      container.jdbcUrl,
      container.username,
      container.password
    )

    val insertProject = new InsertProject[IO](tx)
    val createUser = new CreateUser[IO](tx)
    val insertTask = new InsertTask[IO](tx)
    val updateTask = new TaskInsertUpdate[IO](tx)

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