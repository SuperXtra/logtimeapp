package service.project

import java.time.{LocalDateTime, ZonedDateTime}

import cats.effect.IO
import error.{AppError, ProjectDeleteUnsuccessful}
import models.model.{Project, User}
import org.scalatest.GivenWhenThen
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import repository.project.{CheckIfIsProjectOwner, DeleteProjectWithTasks, FindActiveProjectById, FindProjectById}
import repository.task.DeleteTasks
import repository.user.{CreateUser, GetExistingUserId, UserById}
import service.user.UserCreate
import cats.implicits._
import models.request.{CreateProjectRequest, DeleteProjectRequest, DeleteTaskRequest}

class ProjectDeactivateTest extends AnyFlatSpec with Matchers with GivenWhenThen {

  it should "deactivate project" in new Context {
    Given("user id, deactivated project id, project and result of deactivation = task count")
    val userId = Some(1)
    val deactivatedProjectResult = ().asRight
    val project = Some(Project(1,1,"Test project name", LocalDateTime.now(), Some(LocalDateTime.now().plusHours(2)), Some(true)))
    val deactivateTaskResult = 3.asRight
    val isOwner = true.asRight

    And("a service will deactivate project with tasks")
    val deactivateProject = serviceUnderTest(userId, deactivatedProjectResult, project, deactivateTaskResult, isOwner)

    val deleteProjectRequest = DeleteProjectRequest(
      projectName = "Test project name"
    )

    When("deactivating project")
    val result: Either[AppError, Unit] = deactivateProject(deleteProjectRequest, "dkjasjl67kadsjk").unsafeRunSync()

    Then("returns unit")
    result shouldBe Right(())
  }

  it should "should not deactivate project" in new Context {
    Given("user id, deactivated project id, project and result of deactivation = task count")
    val userId = Some(1)
    val deactivatedProjectResult = ProjectDeleteUnsuccessful.asLeft
    val project = Some(Project(1,1,"Test project name", LocalDateTime.now(), Some(LocalDateTime.now().plusHours(2)), Some(true)))
    val deactivateTaskResult = 3.asRight
    val isOwner = true.asRight

    And("a service will not deactivate project with tasks")
    val deactivateProject = serviceUnderTest(userId, deactivatedProjectResult, project, deactivateTaskResult, isOwner)

    val deleteProjectRequest = DeleteProjectRequest(
      projectName = "Test project name"
    )

    When("deactivating project")
    val result: Either[AppError, Unit] = deactivateProject(deleteProjectRequest, "dkjasjl32kads123jk").unsafeRunSync()

    Then("returns project delete unsuccessful")
    result shouldBe Left(ProjectDeleteUnsuccessful)
  }

  private trait Context {

    def serviceUnderTest(
                          userId: Option[Int],
                          deactivatedProjectResult: Either[AppError, Unit],
                          project: Option[Project],
                          deactivateTaskResult: Either[AppError, Int],
                          isProjectOwner: Either[AppError, Boolean]
                        ): ProjectDeactivate[IO] = {

      val getUserId = new GetExistingUserId[IO](null) {
        override def apply(userIdentification: String): IO[Option[Int]] = userId.pure[IO]
      }
      val deactivateProject = new DeleteProjectWithTasks[IO](null) {
        override def apply(userId: Int, projectName: String, projectId: Int, deleteTime: ZonedDateTime): IO[Either[AppError, Unit]] = deactivatedProjectResult.pure[IO]
      }
      val findProject = new FindProjectById[IO](null) {
        override def apply(projectName: String): IO[Option[Project]] = project.pure[IO]
      }

      val checkIfIsProjectOwner = new CheckIfIsProjectOwner[IO](null){
        override def apply(userId: Int, projectName: String): IO[Either[AppError, Boolean]] = ???
      }


      new ProjectDeactivate[IO](getUserId, deactivateProject, findProject, checkIfIsProjectOwner)
    }
  }
}
