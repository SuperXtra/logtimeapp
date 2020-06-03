package service.task

import java.time.{LocalDateTime, ZoneOffset, ZonedDateTime}

import cats.effect.IO
import errorMessages.{AppBusinessError, ProjectNotFound, ProjectUpdateUnsuccessful, TaskDeleteUnsuccessful, TaskNotFound, TaskUpdateUnsuccessful, UserNotFound}
import models.model.{Project, Task, TaskToUpdate}
import models.request.{LogTaskRequest, UpdateTaskRequest}
import org.scalatest.GivenWhenThen
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import repository.task.{DeleteTask, GetTask, GetUserTask, CreateTask, UpdateTask}
import repository.user.GetUserId
import cats.implicits._

class TaskUpdateTaskQueriesTest extends AnyFlatSpec with Matchers with GivenWhenThen {

  it should "update task" in new Context {

    Given("user wants to log work")
    val userId = 456

    val updateTaskRequest = UpdateTaskRequest(
      oldTaskDescription= "String",
      newTaskDescription= "String1",
      startTime= Some(ZonedDateTime.now(ZoneOffset.UTC)),
      durationTime = 20L,
      volume= Some(4),
      comment= None
    )

    val task = Task(
      id = 283,
      projectId = 123,
      userId = userId,
      taskDescription =updateTaskRequest.oldTaskDescription,
      startTime = updateTaskRequest.startTime.get.toLocalDateTime,
      endTime = updateTaskRequest.startTime.get.plusMinutes(updateTaskRequest.durationTime).toLocalDateTime,
      createTime= ZonedDateTime.now(ZoneOffset.UTC).toLocalDateTime,
      duration = updateTaskRequest.durationTime.toInt,
      volume = updateTaskRequest.volume,
      comment = updateTaskRequest.comment,
      deleteTime = none,
      active = Some(false)
    )

    And("a service will find  user id, user_task, delete old task, insert updated one and return created task id")
    val updateWork = serviceUnderTest(
      userId.some,
      task.some,
      1.asRight,
      ().asRight
    )

    When("logging work")
    val result = updateWork((updateTaskRequest), "test string").unsafeRunSync

    Then("returns created task")
    result shouldBe Right(())

  }

  it should "not allow to update work if user does not exist" in new Context {

    Given("user tries to log work under not existing project")
    val updateTaskRequest = UpdateTaskRequest(
      oldTaskDescription= "String",
      newTaskDescription= "String1",
      startTime= Some(ZonedDateTime.now(ZoneOffset.UTC)),
      durationTime = 20L,
      volume= Some(4),
      comment= None
    )
    And("a service that can't find specified user id and task")
    val updateTask = serviceUnderTest(
      userId = None,
      userTask = None,
      taskDeleteResult = TaskDeleteUnsuccessful().asLeft,
      taskUpdateResult = TaskUpdateUnsuccessful().asLeft
    )

    When("updating work")
    val result = updateTask(updateTaskRequest, "test string").unsafeRunSync

    Then("returns error message: project not found")
    result shouldBe Left(UserNotFound())
  }

  it should "not allow to update work if user task does not exist" in new Context {

    Given("user tries to log work under not existing project")
    val updateTaskRequest = UpdateTaskRequest(
      oldTaskDescription= "String",
      newTaskDescription= "String1",
      startTime= Some(ZonedDateTime.now(ZoneOffset.UTC)),
      durationTime = 20L,
      volume= Some(4),
      comment= None
    )
    And("a service that can't find specified user id and task")
    val updateTask = serviceUnderTest(
      userId = Some(1),
      userTask = None,
      taskDeleteResult = TaskDeleteUnsuccessful().asLeft,
      taskUpdateResult = ().asRight
    )

    When("updating work")
    val result = updateTask(updateTaskRequest, "test string").unsafeRunSync

    Then("returns error message: project not found")
    result shouldBe Left(TaskNotFound())
  }


  it should "not allow to update work if task delete unsuccessful" in new Context {

    Given("user tries to log work under not existing project")
    val updateTaskRequest = UpdateTaskRequest(
      oldTaskDescription= "String",
      newTaskDescription= "String1",
      startTime= Some(ZonedDateTime.now(ZoneOffset.UTC)),
      durationTime = 20L,
      volume= Some(4),
      comment= None
    )

    val task = Task(
      id = 283,
      projectId = 123,
      userId = 1,
      taskDescription =updateTaskRequest.oldTaskDescription,
      startTime = updateTaskRequest.startTime.get.toLocalDateTime,
      endTime = updateTaskRequest.startTime.get.plusMinutes(updateTaskRequest.durationTime).toLocalDateTime,
      createTime= ZonedDateTime.now(ZoneOffset.UTC).toLocalDateTime,
      duration = updateTaskRequest.durationTime.toInt,
      volume = updateTaskRequest.volume,
      comment = updateTaskRequest.comment,
      deleteTime = none,
      active = Some(false)
    )
    And("a service that can't find specified user id and task")
    val updateTask = serviceUnderTest(
      userId = Some(1),
      userTask = Some(task),
      taskDeleteResult = TaskDeleteUnsuccessful().asLeft,
      taskUpdateResult = TaskUpdateUnsuccessful().asLeft
    )

    When("updating work")
    val result = updateTask(updateTaskRequest, "test string").unsafeRunSync

    Then("returns error message: project not found")
    result shouldBe Left(TaskUpdateUnsuccessful())
  }



  private trait Context {
    def serviceUnderTest(
                          userId: Option[Int],
                          userTask: Option[Task],
                          taskDeleteResult: Either[AppBusinessError, Int],
                          taskUpdateResult: Either[TaskUpdateUnsuccessful, Unit]
                        ): TaskUpdate[IO] = {

      val getUserId = new GetUserId[IO](null) {
        override def apply(userIdentification: String): IO[Option[Int]] = userId.pure[IO]
      }
      val getUserTask = new GetUserTask[IO](null) {
        override def apply(taskDescription: String, userId: Long): IO[Option[Task]] = userTask.pure[IO]
      }

      val taskUpdate = new UpdateTask[IO](null) {
        override def apply(toUpdate: TaskToUpdate, timestamp: LocalDateTime, taskDescription: String, projectId: Long, userId: Long): IO[Either[TaskUpdateUnsuccessful, Unit]] = taskUpdateResult.pure[IO]
      }

      new TaskUpdate(getUserId, getUserTask, taskUpdate)
    }
  }

}
