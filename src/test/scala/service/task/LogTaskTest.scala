package service.task

import java.time.{LocalDateTime, ZoneOffset, ZonedDateTime}

import cats.effect._
import cats.implicits._
import error.{LogTimeAppError, ProjectNotFound}
import models.model.{Project, Task}
import models.request.LogTaskRequest
import org.scalatest.GivenWhenThen
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import repository.project.GetProjectByName
import repository.task.{GetTask, CreateTask}
import repository.user.GetUserByUUID

class LogTaskTest  extends AnyFlatSpec with Matchers with GivenWhenThen {

  it should "log work" in new Context {

    Given("user wants to log work")
    val project = Project(1,123,"test",LocalDateTime.now(),None,None)
    val userId = 456
    val workDone = LogTaskRequest(
      projectName = "",
      taskDescription = "",
      startTime = ZonedDateTime.now(ZoneOffset.UTC).minusDays(3),
      durationTime = 0,
      volume = none,
      comment = none
    )
    val task = Task(
      id = 283,
      projectId = project.id,
      userId = userId,
      taskDescription =workDone.taskDescription,
      startTime = workDone.startTime.toLocalDateTime,
      endTime = workDone.startTime.plusMinutes(workDone.durationTime).toLocalDateTime,
      createTime= ZonedDateTime.now(ZoneOffset.UTC).toLocalDateTime,
      duration = workDone.durationTime.toInt,
      volume = workDone.volume,
      comment = workDone.comment,
      deleteTime = none,
      active = Some(false)
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
      durationTime = 0,
      volume = none,
      comment = none
    )
    And("a service that can't find specified project")
    val logWork = serviceUnderTest(
      project = none,
      userId = None,
      task = None,
      insertTaskResult = 0.asRight
    )

    When("logging work")
    val result = logWork(workDone, "test string").unsafeRunSync

    Then("returns error message: project not found")
    result shouldBe Left(ProjectNotFound)
  }

  private trait Context {

    def serviceUnderTest(project: Option[Project],
                         userId: Option[Int],
                         task: Option[Task],
                         insertTaskResult: Either[LogTimeAppError, Int]): LogTask[IO] = {

      val getProjectId = new GetProjectByName[IO](null) {
        override def apply(projectName: String): IO[Option[Project]] = project.pure[IO]
      }

      val getUserId = new GetUserByUUID[IO](null) {
        override def apply(userIdentification: String): IO[Option[Int]] = userId.pure[IO]
      }

      val insertTask = new CreateTask[IO](null) {
        override def apply(create: LogTaskRequest, projectId: Long, userId: Long, startTime: LocalDateTime): IO[Either[LogTimeAppError, Int]] = insertTaskResult.pure[IO]
      }

      val getTask = new GetTask[IO](null) {Long
        override def apply(id: Long): IO[Option[Task]] = task.pure[IO]
      }
      new LogTask(getProjectId, getUserId, insertTask, getTask)
    }
  }
}