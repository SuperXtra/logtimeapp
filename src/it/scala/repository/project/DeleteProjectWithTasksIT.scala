package repository.project

import java.time.{LocalDateTime, ZoneOffset, ZonedDateTime}
import java.util.UUID

import cats.effect.{ContextShift, IO}
import com.dimafeng.testcontainers.{ForAllTestContainer, PostgreSQLContainer}
import db.InitializeDatabase
import doobie.util.ExecutionContexts
import error._
import models._
import models.request.LogTaskRequest
import org.scalatest.{BeforeAndAfterEach, GivenWhenThen}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import repository.task.{CreateTask, GetTask}
import repository.user.InsertUser
import slick.jdbc.PostgresProfile.api._
import db.RunDBIOAction._

class DeleteProjectWithTasksIT extends AnyFlatSpec with Matchers with GivenWhenThen with ForAllTestContainer with BeforeAndAfterEach {

  override val container = new PostgreSQLContainer()

  it should "delete project with its tasks" in new Context {

    Given("existing user")
    val userId = createUser(UUID.randomUUID().toString).exec.unsafeRunSync().right.get

    And("existing project")
    val projectName = "test_project"
    val projectId = insertProject(projectName, userId).exec.unsafeRunSync()

    And("creating tasks")
    val req1 = LogTaskRequest(projectName, "test description 1", ZonedDateTime.now(ZoneOffset.UTC), TaskDuration(50), None, None)
    val req2 = LogTaskRequest(projectName, "test description 2", ZonedDateTime.now(ZoneOffset.UTC).minusDays(5), TaskDuration(50), None, None)
    val task1 = createTask(req1, projectId.right.get, userId, LocalDateTime.now()).exec.unsafeRunSync()
    val task2 = createTask(req2, projectId.right.get, userId, LocalDateTime.now()).exec.unsafeRunSync()

    val time = LocalDateTime.now()

    And("delete project")
    delete(userId, projectName, projectId.right.get, time).exec.unsafeRunSync()

    When("trying to fetch deleted project and tasks")
    val project = findProject(projectName).exec.unsafeRunSync
    val firstTask = getTask(task1.right.get).exec.unsafeRunSync
    val secondTask = getTask(task2.right.get).exec.unsafeRunSync

    Then("it should return true and active  = false for tasks")
    project shouldBe Left(ProjectNotFound)
    firstTask.right.get.active shouldBe Some(Active(false))
    secondTask.right.get.active shouldBe Some(Active(false))
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
    val createUser = new InsertUser[IO]()
    val createTask = new CreateTask[IO]
    val delete = new DeleteProjectWithTasks[IO]
    val findProject = new GetProjectByName[IO]
    val getTask = new GetTask[IO]

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
