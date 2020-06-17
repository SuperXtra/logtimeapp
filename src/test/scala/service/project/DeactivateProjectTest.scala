package service.project

import java.time.{LocalDateTime, ZonedDateTime}

import cats.effect.IO
import error.{LogTimeAppError, ProjectDeleteUnsuccessful}
import models.model.{Project, User}
import org.scalatest.GivenWhenThen
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import repository.project.{IsProjectOwner, DeleteProjectWithTasks, GetProjectByName}
import repository.task.DeleteTasks
import repository.user.{InsertUser, GetUserByUUID, GetUserById}
import service.user.CreateUser
import cats.implicits._
import models.request.{CreateProjectRequest, DeleteProjectRequest, DeleteTaskRequest}

class DeactivateProjectTest extends AnyFlatSpec with Matchers with GivenWhenThen {

  it should "deactivate project" in new Context {
    Given("user id, deactivated project id, project and result of deactivation = task count")
    val userId = Some(1)
    val deactivatedProjectResult = ().asRight
    val project = Project(1,1,"Test project name", LocalDateTime.now(), Some(LocalDateTime.now().plusHours(2)), Some(true))
    val isOwner = true.asRight

    And("a service will deactivate project with tasks")
    val deactivateProject = serviceUnderTest(userId, deactivatedProjectResult, project.some, isOwner)

    val deleteProjectRequest = DeleteProjectRequest(projectName = "Test project name")

    When("deactivating project")
    val result = deactivateProject(deleteProjectRequest.projectName, "dkjasjl67kadsjk").unsafeRunSync()

    Then("returns response that deactivation was successful")
    result.isRight shouldBe true
  }

  it should "should not deactivate project" in new Context {
    Given("user id, deactivated project id, project and result of deactivation = task count")
    val userId = Some(1)
    val deactivatedProjectResult = ProjectDeleteUnsuccessful.asLeft
    val project = Project(1,1,"Test project name", LocalDateTime.now(), Some(LocalDateTime.now().plusHours(2)), Some(true))
    val isOwner = true.asRight

    And("a service will not deactivate project with tasks")
    val deactivateProject = serviceUnderTest(userId, deactivatedProjectResult, project.some, isOwner)

    val deleteProjectRequest = DeleteProjectRequest(
      projectName = "Test project name"
    )

    When("deactivating project")
    val result = deactivateProject(deleteProjectRequest.projectName, "dkjasjl32kads123jk").unsafeRunSync()

    Then("returns project delete unsuccessful error")
    result shouldBe Left(ProjectDeleteUnsuccessful)
  }

  private trait Context {

    def serviceUnderTest(
                          userId: Option[Int],
                          deactivatedProjectResult: Either[LogTimeAppError, Unit],
                          project: Option[Project],
                          isProjectOwner: Either[LogTimeAppError, Boolean]
                        ): DeactivateProject[IO] = {

      val getUserId = new GetUserByUUID[IO](null) {
        override def apply(userIdentification: String): IO[Option[Int]] = userId.pure[IO]
      }
      val deactivateProject = new DeleteProjectWithTasks[IO](null) {
        override def apply(userId: Int, projectName: String, projectId: Int, deleteTime: LocalDateTime): IO[Either[LogTimeAppError, Unit]] = deactivatedProjectResult.pure[IO]
      }
      val findProject = new GetProjectByName[IO](null) {
        override def apply(projectName: String): IO[Option[Project]] = project.pure[IO]
      }

      val checkIfIsProjectOwner = new IsProjectOwner[IO](null){
        override def apply(userId: Int, projectName: String): IO[Either[LogTimeAppError, Boolean]] = isProjectOwner.pure[IO]
      }

      new DeactivateProject[IO](getUserId, deactivateProject, findProject, checkIfIsProjectOwner)
    }
  }
}
