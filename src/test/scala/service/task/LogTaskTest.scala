package service.task

import java.time.{LocalDateTime, ZoneOffset, ZonedDateTime}

import akka.event.{MarkerLoggingAdapter, NoMarkerLogging}
import cats.effect._
import cats.implicits._
import error.{LogTimeAppError, ProjectNotFound}
import models.{Active, ProjectId, TaskDuration, TaskId, UserId}
import models.model.{Project, Task}
import models.request.LogTaskRequest
import org.scalatest.GivenWhenThen
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import repository.project.GetProjectByName
import repository.task.{CreateTask, GetTask}
import repository.user.GetUserByUUID

class LogTaskTest  extends AnyFlatSpec with Matchers with GivenWhenThen {

  it should "log work" in new Context {

    Given("user wants to log work")
    val project = Project(ProjectId(1),UserId(123),"test",LocalDateTime.now(),None,None)
    val userId = UserId(456)
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
      userId = userId,
      taskDescription =workDone.taskDescription,
      startTime = workDone.startTime.toLocalDateTime,
      endTime = workDone.startTime.plusMinutes(workDone.durationTime.value).toLocalDateTime,
      createTime= ZonedDateTime.now(ZoneOffset.UTC).toLocalDateTime,
      duration = workDone.durationTime,
      volume = workDone.volume,
      comment = workDone.comment,
      deleteTime = none,
      active = Some(Active(false))
    )

    And("a service will find project id, user id, insert and return created task task")
    val logWork = serviceUnderTest(
      project = project.some,
      userId = userId.some,
      task = task.some,
      insertTaskResult = task.id.asRight
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
      startTime = ZonedDateTime.of(2020,5,24,21,0,0,0,ZoneOffset.UTC),
      durationTime = TaskDuration(0),
      volume = none,
      comment = none
    )
    And("a service that can't find specified project")
    val logWork = serviceUnderTest(
      project = none,
      userId = None,
      task = None,
      insertTaskResult = TaskId(0).asRight
    )

    When("logging work")
    val result = logWork(workDone, "test string").unsafeRunSync

    Then("returns error message: project not found")
    result shouldBe Left(ProjectNotFound)
  }

  private trait Context {

    implicit lazy val logger: MarkerLoggingAdapter = NoMarkerLogging

    def serviceUnderTest(project: Option[Project],
                         userId: Option[UserId],
                         task: Option[Task],
                         insertTaskResult: Either[LogTimeAppError, TaskId]): LogTask[IO] = {

      val getProjectId = new GetProjectByName[IO](null) {
        override def apply(projectName: String): IO[Option[Project]] = project.pure[IO]
      }

      val getUserId = new GetUserByUUID[IO](null) {
        override def apply(userIdentification: String): IO[Option[UserId]] = userId.pure[IO]
      }

      val insertTask = new CreateTask[IO](null) {

        override def apply(create: LogTaskRequest, projectId: ProjectId, userId: UserId, startTime: LocalDateTime): IO[Either[LogTimeAppError, TaskId]] = insertTaskResult.pure[IO]}

      val getTask = new GetTask[IO](null) {
        override def apply(id: TaskId): IO[Option[Task]] = task.pure[IO]
      }
      new LogTask(getProjectId, getUserId, insertTask, getTask)
    }
  }
}