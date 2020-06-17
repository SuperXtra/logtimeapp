package service.task

import java.time._

import cats.effect._
import cats.implicits._
import error._
import models.model._
import models.request._
import org.scalatest.GivenWhenThen
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import repository.project.GetProjectByName
import repository.task._
import repository.user.GetUserByUUID

class TaskDeleteTestDeactivateTaskQueries extends AnyFlatSpec with Matchers with GivenWhenThen {

  it should "delete task" in new Context {
    Given("user wants to delete task")
    val project = Project(1, 123, "test", LocalDateTime.now(), None, None)
    val userId = 232
    val taskDeleteResult = 1

    And("a service will find project id, user, delete(update) task for that data and return 1")
    val deleteTask = serviceUnderTest(
      project = Some(project),
      userId = userId.some,
      taskDeleteResult = taskDeleteResult.asRight
    )

    val deleteTaskRequest = DeleteTaskRequest(
      taskDescription = "Test task description",
      projectName = "test project name"
    )

    When("Deleting task")
    val result = deleteTask(deleteTaskRequest.taskDescription, deleteTaskRequest.projectName, "dsaadsij12312").unsafeRunSync()

    Then("returns number of rows updated")
    result shouldBe Right(1)
  }

  it should "not allow to delete task if project dose not exist" in new Context {
    Given("user wants to delete task")
    val project = ProjectNotFound


    And("a service that cannot find specified project and task")
    val deleteTask = serviceUnderTest(
      project = None,
      userId = None,
      taskDeleteResult = 0.asRight
    )

    val deleteTaskRequest = DeleteTaskRequest(
      taskDescription = "Test task description",
      projectName = "Not existing project name"
    )

    When("Deleting task")
    val result = deleteTask(deleteTaskRequest.taskDescription, deleteTaskRequest.projectName, "dsaadsij12312").unsafeRunSync()

    Then("returns number of rows updated")
    result shouldBe Left(ProjectNotFound)
  }


  private trait Context {

    def serviceUnderTest(project: Option[Project],
                         userId: Option[Int],
                         taskDeleteResult: Either[LogTimeAppError, Int]
                        ): DeactivateTask[IO] = {


      val getProjectId = new GetProjectByName[IO](null) {
        override def apply(projectName: String): IO[Option[Project]] = project.pure[IO]
      }
      val getUserId = new GetUserByUUID[IO](null) {
        override def apply(userIdentification: String): IO[Option[Int]] = userId.pure[IO]
      }
      val delete = new DeleteTask[IO](null) {
        override def apply(taskDescription: String, projectId: Long, userId: Long, deleteTime: LocalDateTime): IO[Either[LogTimeAppError, Int]] = taskDeleteResult.pure[IO]
      }

      new DeactivateTask(getProjectId, getUserId, delete)
    }
  }


}
