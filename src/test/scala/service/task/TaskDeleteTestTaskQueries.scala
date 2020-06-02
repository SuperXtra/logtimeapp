package service.task

import java.time._

import cats.effect._
import cats.implicits._
import errorMessages._
import models.model._
import models.request._
import org.scalatest.GivenWhenThen
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import repository.project.FindProjectByName
import repository.task._
import repository.user.GetExistingUserId

class TaskDeleteTestTaskQueries extends AnyFlatSpec with Matchers with GivenWhenThen {

  it should "delete task" in new Context {
    Given("user wants to delete task")
    val project = Project(1, 123, "test", LocalDateTime.now(), None, None)
    val userId = 232
    val taskDeleteResult = 1


    And("a service will find project id, user, delete(update) task for that data and return 1")
    val deleteTask = serviceUnderTest(
      project = Right(project),
      userId = userId.some,
      taskDeleteResult = taskDeleteResult.asRight
    )

    val deleteTaskRequest = DeleteTaskRequest(
      taskDescription = "Test task description",
      projectName = "test project name"
    )

    When("Deleting task")
    val result: Either[AppBusinessError, Int] = deleteTask(deleteTaskRequest, "dsaadsij12312").unsafeRunSync()

    Then("returns number of rows updated")
    result shouldBe Right(1)
  }

  it should "not allow to delete task if project dose not exist" in new Context {
    Given("user wants to delete task")
    val project = ProjectNotFound()


    And("a service that cannot find specified project and task")
    val deleteTask = serviceUnderTest(
      project = project.asLeft,
      userId = None,
      taskDeleteResult = 0.asRight
    )

    val deleteTaskRequest = DeleteTaskRequest(
      taskDescription = "Test task description",
      projectName = "Not existing project name"
    )

    When("Deleting task")
    val result: Either[AppBusinessError, Int] = deleteTask(deleteTaskRequest, "dsaadsij12312").unsafeRunSync()

    Then("returns number of rows updated")
    result shouldBe Left(ProjectNotFound())
  }


  private trait Context {

    def serviceUnderTest(project: Either[AppBusinessError, Project],
                         userId: Option[Int],
                         taskDeleteResult: Either[AppBusinessError, Int]
                        ): TaskDelete[IO] = {


      val getProjectId = new FindProjectByName[IO](null) {
        override def apply(projectName: String): IO[Either[AppBusinessError, Project]] = project.pure[IO]
      }
      val getUserId = new GetExistingUserId[IO](null) {
        override def apply(userIdentification: String): IO[Option[Int]] = userId.pure[IO]
      }
      val delete = new DeleteTask[IO](null) {
        override def apply(taskDescription: String, projectId: Long, userId: Long, deleteTime: LocalDateTime): IO[Either[AppBusinessError, Int]] = taskDeleteResult.pure[IO]
      }

      new TaskDelete(getProjectId, getUserId, delete)
    }
  }


}
