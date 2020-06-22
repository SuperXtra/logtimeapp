package service.task

import java.time.{LocalDateTime, ZoneOffset, ZonedDateTime}

import akka.event.{MarkerLoggingAdapter, NoMarkerLogging}
import cats.effect._
import cats.implicits._
import error.{LogTimeAppError, ProjectNotFound, TaskNotFound, UserNotFound}
import models.{Active, ProjectId, TaskDuration, TaskId, UserId}
import models.model.{Project, Task, User}
import models.request.LogTaskRequest
import org.scalatest.GivenWhenThen
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import repository.project.GetProjectByName
import repository.task.{CreateTask, GetTask}
import repository.user.GetUserByUUID
import service.SetUp
import slick.dbio._

class LogTaskTest  extends AnyFlatSpec with Matchers with GivenWhenThen {

  it should "log work" in new Context {

    Given("user wants to log work")
    val project = Project(ProjectId(1), UserId(123), "test", LocalDateTime.now(), None, None)
    val user = User(UserId(456), "123")
    val workDone = LogTaskRequest(
      projectName = "",
      taskDescription = "",
      startTime = ZonedDateTime.now(ZoneOffset.UTC).minusDays(3),
      durationTime = TaskDuration(0),
      volume = none,
      comment = none
    )
    val task = Task(
      id = TaskId(283),
      projectId = project.id,
      userId = user.userId,
      taskDescription = workDone.taskDescription,
      startTime = workDone.startTime.toLocalDateTime,
      endTime = workDone.startTime.plusMinutes(workDone.durationTime.value).toLocalDateTime,
      createTime = ZonedDateTime.now(ZoneOffset.UTC).toLocalDateTime,
      duration = workDone.durationTime,
      volume = workDone.volume,
      comment = workDone.comment,
      deleteTime = none,
      active = Some(Active(false))
    )

    And("a service will find project id, user id, insert and return created task task")
    val logWork = serviceUnderTest(
      project = project.asRight,
      userId = user.asRight,
      task = task.id.asRight,
      insertTaskResult = task.asRight
    )

    When("logging work")
    val result = logWork((workDone), "test string").unsafeRunSync

    Then("returns created task")
    result shouldBe Right(task)
  }

  it should "not allow to log work if project does not exist" in new Context {

    Given("user tries to log work under not existing project")
    val project = None
    val workDone = LogTaskRequest(
      projectName = "",
      taskDescription = "",
      startTime = ZonedDateTime.of(2020, 5, 24, 21, 0, 0, 0, ZoneOffset.UTC),
      durationTime = TaskDuration(0),
      volume = none,
      comment = none
    )
    And("a service that can't find specified project")
    val logWork = serviceUnderTest(
      project = ProjectNotFound.asLeft,
      userId = UserNotFound.asLeft,
      task = TaskNotFound.asLeft,
      insertTaskResult = TaskNotFound.asLeft
    )

    When("logging work")
    val result = logWork(workDone, "test string").unsafeRunSync

    Then("returns error message: project not found")
    result shouldBe Left(ProjectNotFound)
  }

  private trait Context extends SetUp {

    def serviceUnderTest(project: Either[LogTimeAppError, Project],
                         userId: Either[LogTimeAppError, User],
                         task: Either[LogTimeAppError, TaskId],
                         insertTaskResult: Either[LogTimeAppError, Task]): LogTask[IO] = {

      val getProjectId = new GetProjectByName[IO] {
        override def apply(projectName: String) = DBIOAction.successful(project)
      }

      val getUserId = new GetUserByUUID[IO] {
        override def apply(userIdentification: String) = DBIOAction.successful(userId)
      }

      val insertTask = new CreateTask[IO] {
        override def apply(create: LogTaskRequest, projectId: ProjectId, userId: UserId, startTime: LocalDateTime) = DBIOAction.successful(task)
      }
      val getTask = new GetTask[IO] {
        override def apply(id: TaskId) = DBIOAction.successful(insertTaskResult)
      }
      new LogTask(getProjectId, getUserId, insertTask, getTask)
    }
  }
}