package service

import java.time.{LocalDateTime, ZoneOffset, ZonedDateTime}

import cats.implicits._
import cats.effect._
import models.model.{ProjectTb, TaskTb}
import service.task._
import doobie.util.ExecutionContexts
import doobie.util.transactor.Transactor
import error.{AppError, ProjectNotFound}
import models.request.LogTaskRequest
import org.scalatest.matchers.should.Matchers
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.GivenWhenThen
import org.joda.time.DateTime
import repository.project.FindProjectById
import repository.task.{GetTask, InsertTask}
import repository.user.{GetExistingUserId, UserById}

class LogTaskTest  extends AnyFlatSpec with Matchers with GivenWhenThen {

  it should "log work" in new Context {

    Given("user wants to log work")
    val project = ProjectTb(1,123,"test",LocalDateTime.now(),None,None)
    val userId = 456
    val workDone = LogTaskRequest(
      projectName = "",
      userIdentification = "",
      taskDescription = "",
      startTime = ZonedDateTime.now(ZoneOffset.UTC).minusDays(3),
      durationTime = 0,
      volume = none,
      comment = none
    )
    val task = TaskTb(
      id = 283,
      projectId = project.id,
      userId = userId,
      taskDescription =workDone.taskDescription,
      startTime = workDone.startTime.toLocalDateTime,
      endTime = workDone.startTime.plusMinutes(workDone.durationTime).toLocalDateTime,
      createTime= ZonedDateTime.now(ZoneOffset.UTC).toLocalDateTime,
      duration = workDone.durationTime,
      volume = workDone.volume,
      comment = workDone.comment,
      deleteTime = none,
      active = false
    )

    And("a service will find project id, user id, insert and return created task task")
    val logWork = serviceUnderTest(
      project = Some(project),
      userId = userId.toLong.some,
      task = task.some,
      insertTaskResult = task.id.toLong.asRight
    )

    When("logging work")
    val result = logWork(workDone).unsafeRunSync

    Then("returns created task")
    result shouldBe Right(task)

  }

  it should "not allow to log work if project does not exist" in new Context {

    Given("user tries to log work under not existing project")
    val project = None
    val workDone = LogTaskRequest(
      projectName = "",
      userIdentification = "",
      taskDescription = "",
      startTime = ZonedDateTime.of(2020,5,24,21,0,0,0,ZoneOffset.UTC),
      durationTime = 0,
      volume = none,
      comment = none
    )
    And("a service that can't find specified project")
    val logWork = serviceUnderTest(
      project = None,
      userId = None,
      task = None,
      insertTaskResult = 0L.asRight
    )

    When("logging work")
    val result = logWork(workDone).unsafeRunSync

    Then("returns error message: project not found")
    result shouldBe Left(ProjectNotFound)
  }

  // not allow to log work if user id does not exist

  // return error if insert was unsuccessful

  // return error if insert was unsuccessful - 2

  // return error could not create task

  private trait Context {

    def serviceUnderTest(project: Option[ProjectTb],
                         userId: Option[Long],
                         task: Option[TaskTb],
                         insertTaskResult: Either[AppError, Long]): LogTask[IO] = {

      val getProjectId = new FindProjectById[IO](null) {
        override def apply(projectName: String): IO[Option[ProjectTb]] = project.pure[IO]
      }

      val getUserId = new GetExistingUserId[IO](null) {
        override def apply(userIdentification: String): IO[Option[Long]] = userId.pure[IO]
      }

      val insertTask = new InsertTask[IO](null) {
        override def apply(create: LogTaskRequest, projectId: Long, userId: Long): IO[Either[AppError, Long]] =
          insertTaskResult.pure[IO]
      }

      val getTask = new GetTask[IO](null) {
        override def apply(id: Long): IO[Option[TaskTb]] = task.pure[IO]
      }
      new LogTask(getProjectId, getUserId, insertTask, getTask)
    }
  }
}