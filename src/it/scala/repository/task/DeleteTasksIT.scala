package repository.task

import java.time.{LocalDateTime, ZoneOffset, ZonedDateTime}
import java.util.UUID

import cats.effect.{ContextShift, IO}
import com.dimafeng.testcontainers.{ForAllTestContainer, PostgreSQLContainer}
import db.InitializeDatabase
import doobie.util.ExecutionContexts
import doobie.util.transactor.Transactor
import models.{Active, TaskDuration}
import models.request.LogTaskRequest
import org.scalatest.{BeforeAndAfterEach, GivenWhenThen}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import repository.project.InsertProject
import repository.user.InsertUser
import slick.jdbc.PostgresProfile.api._
import db.RunDBIOAction._

class DeleteTasksIT extends AnyFlatSpec with Matchers with GivenWhenThen with ForAllTestContainer with BeforeAndAfterEach {

  override val container = new PostgreSQLContainer()

  it should "delete tasks" in new Context {

    Given("existing user")
    val userId = createUser(UUID.randomUUID().toString).exec.unsafeRunSync().right.get

    And("existing project")
    val projectName = "test_project"
    val projectId = insertProject(projectName, userId).exec.unsafeRunSync()

    And("existing tasks")
    val req1 = LogTaskRequest(projectName, "test description 1", ZonedDateTime.now(ZoneOffset.UTC), TaskDuration(50), None, None)
    val req2 = LogTaskRequest(projectName, "test description 2", ZonedDateTime.now(ZoneOffset.UTC).minusDays(5), TaskDuration(50), None, None)
    val task1 = insertTask(req1,projectId.right.get, userId, LocalDateTime.now()).exec.unsafeRunSync()
    val task2 = insertTask(req2,projectId.right.get, userId, LocalDateTime.now()).exec.unsafeRunSync()

    When("deleting inserted tasks")
    deleteTasks(projectId.right.get, LocalDateTime.now()).exec.unsafeRunSync()

    And("fetching information about deleted tasks")
    val result = getTask(task1.right.get).exec.unsafeRunSync().right.get.active
    val result2 = getTask(task2.right.get).exec.unsafeRunSync().right.get.active

    Then("it should return both tasks are inactive")
    result shouldBe Some(Active(false))
    result2 shouldBe Some(Active(false))
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

    val getTask = new GetTask[IO]
    val insertProject = new InsertProject[IO]
    val createUser = new InsertUser[IO]
    val insertTask = new CreateTask[IO]
    val deleteTasks = new DeleteTasks[IO]

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
