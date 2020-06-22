package service.task

import java.time.{LocalDateTime, ZoneOffset, ZonedDateTime}

import cats.effect.IO
import error._
import models.model._
import models.request._
import org.scalatest.GivenWhenThen
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import repository.task._
import repository.user.GetUserByUUID
import cats.implicits._
import models.{Active, ProjectId, TaskDuration, TaskId, UserId, Volume}
import service.SetUp
import slick.dbio._

class TaskUpdateQueriesTestTask extends AnyFlatSpec with Matchers with GivenWhenThen {

  it should "update task" in new Context {

    Given("user user id and update request")
    val user = User(UserId(456),"123")

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
      userId = user.userId,
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
      user.asRight,
      task.asRight,
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
      user = UserNotFound.asLeft,
      userTask = TaskNotFound.asLeft,
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
      user = User(UserId(1), "123").asRight,
      userTask = TaskNotFound.asLeft,
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
      user = User(UserId(1), "123").asRight,
      userTask = task.asRight,
      taskUpdateResult = TaskUpdateUnsuccessful.asLeft
    )

    When("updating work")
    val result = updateTask(updateTaskRequest, "test string").unsafeRunSync

    Then("returns error message: project not found")
    result shouldBe Left(TaskUpdateUnsuccessful )
  }



  private trait Context extends SetUp {

    def serviceUnderTest(
                          user: Either[LogTimeAppError, User],
                          userTask: Either[LogTimeAppError,Task],
                          taskUpdateResult: Either[TaskUpdateUnsuccessful.type , Unit]
                        ): UpdateTask[IO] = {

      val getUserId = new GetUserByUUID[IO] {
        override def apply(userIdentification: String) = DBIOAction.successful(user)
      }
      val getUserTask = new GetUserTask[IO] {
        override def apply(taskDescription: String, userId: UserId) = DBIOAction.successful(userTask)
      }

      val taskUpdate = new ChangeTask[IO] {
        override def apply(toUpdate: TaskToUpdate, timestamp: LocalDateTime, taskDescription: String, projectId: ProjectId, userId: UserId) = DBIOAction.successful(taskUpdateResult)
      }

      new UpdateTask(getUserId, getUserTask, taskUpdate)
    }
  }

}
