package service.task

import java.time._

import cats.effect._
import cats.implicits._
import error._
import models.{DeleteCount, ProjectId, UserId}
import models.model._
import models.request._
import org.scalatest.GivenWhenThen
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import repository.project.GetProjectByName
import repository.task._
import repository.user.GetUserByUUID
import service.SetUp
import slick.dbio._

class TaskDeleteTestDeactivateTaskQueries extends AnyFlatSpec with Matchers with GivenWhenThen {

  it should "delete task" in new Context {
    Given("user wants to delete task")
    val project = Project(ProjectId(1), UserId(123), "test", LocalDateTime.now(), None, None)
    val user = User(UserId(232),"123")
    val taskDeleteResult = DeleteCount(1)

    And("a service will find project id, user, delete(update) task for that data and return 1")
    val deleteTask = serviceUnderTest(
      project = project.asRight,
      userId = user.asRight,
      taskDeleteResult = taskDeleteResult.asRight
    )

    val deleteTaskRequest = DeleteTaskRequest(
      taskDescription = "Test task description",
      projectName = "test project name"
    )

    When("Deleting task")
    val result = deleteTask(deleteTaskRequest.taskDescription, deleteTaskRequest.projectName, "dsaadsij12312").unsafeRunSync()

    Then("returns number of rows updated")
    result shouldBe Right(DeleteCount(1))
  }

  it should "not allow to delete task if project dose not exist" in new Context {
    Given("user wants to delete task")
    val project = ProjectNotFound


    And("a service that cannot find specified project and task")
    val deleteTask = serviceUnderTest(
      project = ProjectNotFound.asLeft,
      userId = UserNotFound.asLeft,
      taskDeleteResult = DeleteCount(0).asRight
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


  private trait Context extends SetUp {

    def serviceUnderTest(project: Either[LogTimeAppError,Project],
                         userId: Either[LogTimeAppError, User],
                         taskDeleteResult: Either[LogTimeAppError, DeleteCount]
                        ): DeactivateTask[IO] = {


      val getProjectId = new GetProjectByName[IO] {
        override def apply(projectName: String) = DBIOAction.successful(project)
      }
      val getUserId = new GetUserByUUID[IO] {
        override def apply(userIdentification: String) = DBIOAction.successful(userId)
      }
      val delete = new DeleteTask[IO] {
        override def apply(taskDescription: String, projectId: ProjectId, userId: UserId, deleteTime: LocalDateTime) = DBIOAction.successful(taskDeleteResult)
      }

      new DeactivateTask(getProjectId, getUserId, delete)
    }
  }


}
