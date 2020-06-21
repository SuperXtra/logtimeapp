package service.project

import java.time._
import cats.effect.IO
import error.{LogTimeAppError, ProjectDeleteUnsuccessful}
import models.model.{Project, User}
import org.scalatest.GivenWhenThen
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import repository.project.{DeleteProjectWithTasks, GetProjectByName, IsProjectOwner}
import repository.user._
import cats.implicits._
import models.{Active, IsOwner, ProjectId, UserId}
import models.request._
import service.SetUp
import slick.dbio._

class DeactivateProjectTest extends AnyFlatSpec with Matchers with GivenWhenThen {

  it should "deactivate project" in new Context {
    Given("user id, deactivated project id, project and result of deactivation = task count")
    val user = User(UserId(1), "123").asRight
    val deactivatedProjectResult = ().asRight
    val project = Project(ProjectId(1),UserId(1),"Test project name", LocalDateTime.now(), Some(LocalDateTime.now().plusHours(2)), Some(Active(true))).asRight
    val isOwner = IsOwner(true).asRight

    And("a service will deactivate project with tasks")
    val deactivateProject = serviceUnderTest(user, deactivatedProjectResult, project, isOwner)

    val deleteProjectRequest = DeleteProjectRequest(projectName = "Test project name")

    When("deactivating project")
    val result = deactivateProject(deleteProjectRequest.projectName, "dkjasjl67kadsjk").unsafeRunSync()

    Then("returns response that deactivation was successful")
    result.isRight shouldBe true
  }

  it should "should not deactivate project" in new Context {
    Given("user id, deactivated project id, project and result of deactivation = task count")
    val user = User(UserId(1), "123").asRight
    val deactivatedProjectResult = ProjectDeleteUnsuccessful.asLeft
    val project = Project(ProjectId(1),UserId(1),"Test project name", LocalDateTime.now(), Some(LocalDateTime.now().plusHours(2)), Some(Active(true))).asRight
    val isOwner = IsOwner(true).asRight

    And("a service will not deactivate project with tasks")
    val deactivateProject = serviceUnderTest(user, deactivatedProjectResult, project, isOwner)

    val deleteProjectRequest = DeleteProjectRequest(
      projectName = "Test project name"
    )

    When("deactivating project")
    val result = deactivateProject(deleteProjectRequest.projectName, "dkjasjl32kads123jk").unsafeRunSync()

    Then("returns project delete unsuccessful error")
    result shouldBe Left(ProjectDeleteUnsuccessful)
  }

  private trait Context extends SetUp {

    def serviceUnderTest(
                          user: Either[LogTimeAppError, User],
                          deactivatedProjectResult: Either[LogTimeAppError, Unit],
                          project: Either[LogTimeAppError, Project],
                          isProjectOwner: Either[LogTimeAppError, IsOwner]
                        ): DeactivateProject[IO] = {

      val getUserId = new GetUserByUUID[IO] {
        override def apply(userIdentification: String)= DBIOAction.successful(user)
      }
      val deactivateProject = new DeleteProjectWithTasks[IO] {
        override def apply(userId: UserId, projectName: String, projectId: ProjectId, deleteTime: LocalDateTime)=DBIOAction.successful(deactivatedProjectResult)
      }
      val findProject = new GetProjectByName[IO] {
        override def apply(projectName: String) = DBIOAction.successful(project)
      }

      val checkIfIsProjectOwner = new IsProjectOwner[IO] {
        override def apply(userId: UserId, projectName: String) = DBIOAction.successful(isProjectOwner)
      }

      new DeactivateProject[IO](getUserId, deactivateProject, findProject, checkIfIsProjectOwner)
    }
  }
}