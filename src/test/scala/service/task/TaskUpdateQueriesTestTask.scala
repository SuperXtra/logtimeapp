package service.task

import java.time.{LocalDateTime, ZoneOffset, ZonedDateTime}

import cats.effect.IO
import error.{LogTimeAppError, ProjectNotFound, ProjectUpdateUnsuccessful, TaskDeleteUnsuccessful, TaskNotFound, TaskUpdateUnsuccessful, UserNotFound}
import models.model.{Project, Task, TaskToUpdate}
import models.request.{LogTaskRequest, UpdateTaskRequest}
import org.scalatest.GivenWhenThen
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import repository.task.{ChangeTask, CreateTask, DeleteTask, GetTask, GetUserTask}
import repository.user.GetUserByUUID
import cats.implicits._
import models.{Active, ProjectId, TaskDuration, TaskId, UserId, Volume}

class TaskUpdateQueriesTestTask extends AnyFlatSpec with Matchers with GivenWhenThen {

  it should "update task" in new Context {

    Given("user user id and update request")
    val userId = UserId(456)

    val updateTaskRequest = UpdateTaskRequest(
      oldTaskDescription= "String",
      newTaskDescription= "String1",
      startTime= Some(ZonedDateTime.now(ZoneOffset.UTC)),
      durationTime = TaskDuration(20),
      volume= Some(Volume(4)),
      comment= None
    )

    val task = Task(
      id = TaskId(283),
      projectId = ProjectId(123),
      userId = userId,
      taskDescription =updateTaskRequest.oldTaskDescription,
      startTime = updateTaskRequest.startTime.get.toLocalDateTime,
      endTime = updateTaskRequest.startTime.get.plusMinutes(updateTaskRequest.durationTime.value).toLocalDateTime,
      createTime= ZonedDateTime.now(ZoneOffset.UTC).toLocalDateTime,
      duration = updateTaskRequest.durationTime,
      volume = updateTaskRequest.volume,
      comment = updateTaskRequest.comment,
      deleteTime = none,
      active = Some(Active(false))
    )

    And("a service will find  user id, user_task, delete old task, insert updated one and return right confirming that operation was successful")
    val updateWork = serviceUnderTest(
      userId.some,
      task.some,
      1.asRight,
      ().asRight
    )

    When("logging work")
    val result = updateWork((updateTaskRequest), "test string").unsafeRunSync

    Then("returns updated task")
    result.isRight shouldBe true
  }

  it should "not allow to update work if user does not exist" in new Context {

    Given("user tries to log work under not existing project")
    val updateTaskRequest = UpdateTaskRequest(
      oldTaskDescription= "String",
      newTaskDescription= "String1",
      startTime= Some(ZonedDateTime.now(ZoneOffset.UTC)),
      durationTime = TaskDuration(20),
      volume= Some(Volume(4)),
      comment= None
    )
    And("a service that can't find specified user id and task")
    val updateTask = serviceUnderTest(
      userId = None,
      userTask = None,
      taskDeleteResult = TaskDeleteUnsuccessful.asLeft,
      taskUpdateResult = TaskUpdateUnsuccessful.asLeft
    )

    When("updating work")
    val result = updateTask(updateTaskRequest, "test string").unsafeRunSync

    Then("returns error message: project not found")
    result shouldBe Left(UserNotFound)
  }

  it should "not allow to update work if user task does not exist" in new Context {

    Given("user tries to log work under not existing project")
    val updateTaskRequest = UpdateTaskRequest(
      oldTaskDescription= "String",
      newTaskDescription= "String1",
      startTime= Some(ZonedDateTime.now(ZoneOffset.UTC)),
      durationTime = TaskDuration(20),
      volume= Some(Volume(4)),
      comment= None
    )
    And("a service that can't find specified user id and task")
    val updateTask = serviceUnderTest(
      userId = Some(UserId(1)),
      userTask = None,
      taskDeleteResult = TaskDeleteUnsuccessful.asLeft,
      taskUpdateResult = ().asRight
    )

    When("updating work")
    val result = updateTask(updateTaskRequest, "test string").unsafeRunSync

    Then("returns error message: project not found")
    result shouldBe Left(TaskNotFound)
  }


  it should "not allow to update work if task delete unsuccessful" in new Context {

    Given("user tries to log work under not existing project")
    val updateTaskRequest = UpdateTaskRequest(
      oldTaskDescription= "String",
      newTaskDescription= "String1",
      startTime= Some(ZonedDateTime.now(ZoneOffset.UTC)),
      durationTime = TaskDuration(20),
      volume= Some(Volume(4)),
      comment= None
    )

    val task = Task(
      id = TaskId(283),
      projectId = ProjectId(123),
      userId = UserId(1),
      taskDescription =updateTaskRequest.oldTaskDescription,
      startTime = updateTaskRequest.startTime.get.toLocalDateTime,
      endTime = updateTaskRequest.startTime.get.plusMinutes(updateTaskRequest.durationTime.value).toLocalDateTime,
      createTime= ZonedDateTime.now(ZoneOffset.UTC).toLocalDateTime,
      duration = updateTaskRequest.durationTime,
      volume = updateTaskRequest.volume,
      comment = updateTaskRequest.comment,
      deleteTime = none,
      active = Some(Active(false))
    )
    And("a service that can't find specified user id and task")
    val updateTask = serviceUnderTest(
      userId = Some(UserId(1)),
      userTask = Some(task),
      taskDeleteResult = TaskDeleteUnsuccessful.asLeft,
      taskUpdateResult = TaskUpdateUnsuccessful.asLeft
    )

    When("updating work")
    val result = updateTask(updateTaskRequest, "test string").unsafeRunSync

    Then("returns error message: project not found")
    result shouldBe Left(TaskUpdateUnsuccessful )
  }



  private trait Context {
    def serviceUnderTest(
                          userId: Option[UserId],
                          userTask: Option[Task],
                          taskDeleteResult: Either[LogTimeAppError, Int],
                          taskUpdateResult: Either[TaskUpdateUnsuccessful.type , Unit]
                        ): UpdateTask[IO] = {

      val getUserId = new GetUserByUUID[IO](null) {
        override def apply(userIdentification: String): IO[Option[UserId]] = userId.pure[IO]
      }
      val getUserTask = new GetUserTask[IO](null) {
        override def apply(taskDescription: String, userId: UserId): IO[Option[Task]] = userTask.pure[IO]
      }

      val taskUpdate = new ChangeTask[IO](null) {
        override def apply(toUpdate: TaskToUpdate, timestamp: LocalDateTime, taskDescription: String, projectId: ProjectId, userId: UserId): IO[Either[LogTimeAppError, Unit]] = taskUpdateResult.pure[IO]
      }

      new UpdateTask(getUserId, getUserTask, taskUpdate)
    }
  }

}
