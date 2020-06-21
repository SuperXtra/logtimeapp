package service.project

import java.time.LocalDateTime

import akka.event.{MarkerLoggingAdapter, NoMarkerLogging}
import cats.effect.IO
import models.model.{Project, User}
import org.scalatest.GivenWhenThen
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import repository.project.{GetProjectByName, UpdateProjectName}
import repository.user._
import cats.implicits._
import error._
import models.{Active, ProjectId, UserId}
import models.request.{ChangeProjectNameRequest, DeleteProjectRequest}
import service.SetUp
import slick.dbio.{DBIOAction, Effect}
import slick.jdbc
import slick.jdbc.PostgresProfile

class UpdateProjectTest extends AnyFlatSpec with Matchers with GivenWhenThen {

  it should "update project" in new Context {
    Given("user id, result of updating project, updated project")
    val user = User(UserId(1), "123").asRight
    val updateProjectName = "After change"
    val updatedProject = Project(ProjectId(1), UserId(1), updateProjectName, LocalDateTime.now(), Some(LocalDateTime.now().plusHours(2)), Some(Active(true)))

    And("ability to check if project was updated")
    val updateName = ().asRight


    And("a service will update project")
    val updateProject = serviceUnderTest(user, updateName)

    val changeProjectName = ChangeProjectNameRequest(
      oldProjectName = "Test project name",
      projectName = updateProjectName
    )

    When("updating project")
    val result = updateProject(changeProjectName.oldProjectName, changeProjectName.projectName, "uudissdsa2321hjd8fs").unsafeRunSync()

    Then("returns project")
    result.isRight shouldBe true

  }

  it should "not update project" in new Context {
    Given("user id, result of updating project, updated project")
    val user = User(UserId(1), "123").asRight
    val updateName = ProjectNotCreated.asLeft[Unit]

    And("a service will update project")
    val updateProject = serviceUnderTest(user, updateName)

    val changeProjectName = ChangeProjectNameRequest(
      oldProjectName = "Test project name",
      projectName = "After change"
    )

    When("updating project")
    val result = updateProject(changeProjectName.oldProjectName,changeProjectName.projectName, "uudissdsa2321hjd8fs").unsafeRunSync()

    Then("returns project not created error")
    result shouldBe Left(ProjectNotCreated)
  }

  private trait Context extends SetUp{

    def serviceUnderTest(
                          user: Either[LogTimeAppError, User],
                          updateName: Either[LogTimeAppError, Unit]
                        ): UpdateProject[IO] = {

      val userIO = new GetUserByUUID[IO] {
        override def apply(userIdentification: String) = DBIOAction.successful(user)
      }
      val updateProjectName = new UpdateProjectName[IO] {
        override def apply(oldName: String, newName: String, userId: UserId) = DBIOAction.successful(updateName)
      }

      new UpdateProject[IO](userIO, updateProjectName)
    }
  }
}