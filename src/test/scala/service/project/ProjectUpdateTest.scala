package service.project

import java.time.LocalDateTime

import cats.effect.IO
import models.model.{Project, User}
import org.scalatest.GivenWhenThen
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import repository.project.{FindProjectByName, UpdateProjectName}
import repository.user.{CreateUser, GetUserId, UserById}
import service.user.UserCreate
import cats.implicits._
import errorMessages.{AppBusinessError, ProjectNameExists, ProjectNotCreated}
import models.request.{ChangeProjectNameRequest, DeleteProjectRequest}

class ProjectUpdateTest extends AnyFlatSpec with Matchers with GivenWhenThen {


  it should "update project" in new Context {
    Given("user id, result of updating project, updated project")
    val userId = Some(1)
    val updatedProjectResult = ().asRight
    val project = Project(1,1,"TEst project name", LocalDateTime.now(), Some(LocalDateTime.now().plusHours(2)), Some(true))

    And("a service will update project")
    val updateProject = serviceUnderTest(userId, updatedProjectResult,Right(project))

    val changeProjectName = ChangeProjectNameRequest(
      oldProjectName = "Test project name",
      projectName = "After change"
    )

    When("updating project")
    val result: Either[AppBusinessError, Project] = updateProject(changeProjectName, "uudissdsa2321hjd8fs").unsafeRunSync()

    Then("returns project")
    result shouldBe Right(project)
  }

  it should "not update project" in new Context {
    Given("user id, result of updating project, updated project")
    val userId = Some(1)
    val updatedProjectResult = ().asRight
    val project = ProjectNotCreated()

    And("a service will update project")
    val updateProject = serviceUnderTest(userId, updatedProjectResult,Left(project))

    val changeProjectName = ChangeProjectNameRequest(
      oldProjectName = "Test project name",
      projectName = "After change"
    )

    When("updating project")
    val result: Either[AppBusinessError, Project] = updateProject(changeProjectName, "uudissdsa2321hjd8fs").unsafeRunSync()

    Then("returns project")
    result shouldBe Left(ProjectNotCreated())
  }



  private trait Context {

    def serviceUnderTest(
                        userId: Option[Int],
                        updatedProjectResult: Either[AppBusinessError, Unit],
                        project: Either[AppBusinessError, Project]
                        ): ProjectUpdate[IO] = {

      val user = new GetUserId[IO](null) {
        override def apply(userIdentification: String): IO[Option[Int]] = userId.pure[IO]
      }
      val updateProjectName = new UpdateProjectName[IO](null) {
        override def apply(oldName: String, newName: String, userId: Long): IO[Either[AppBusinessError, Unit]] = updatedProjectResult.pure[IO]
      }
      val findProject = new FindProjectByName[IO](null) {
        override def apply(projectName: String): IO[Either[AppBusinessError, Project]] = project.pure[IO]
      }

      new ProjectUpdate[IO](user, updateProjectName, findProject)
    }
  }
}
