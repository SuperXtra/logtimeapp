package service.project

import cats.effect.IO
import error.{LogTimeAppError, ProjectNotCreated, UserNotFound}
import models.model.User
import org.scalatest.GivenWhenThen
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import repository.project.InsertProject
import repository.user.{InsertUser, GetUserByUUID, GetUserById}
import service.user.CreateUser
import cats.implicits._
import models.request.{CreateProjectRequest, DeleteTaskRequest}

class CreateProjectTest extends AnyFlatSpec with Matchers with GivenWhenThen {

  it should "create new project" in new Context {
    Given("user id, inserted project id and request")
    val userId  = Some(2)
    val insertedProjectId = 4.asRight
    val request = CreateProjectRequest("Test project name")

    And("a service will create project and return its id")
    val createProject = serviceUnderTest(userId, insertedProjectId)

    When("creating project")
    val result = createProject(request.projectName, "dsaddas32ndsjkn").unsafeRunSync()

    Then("returns project id")
    result shouldBe Right(4)
  }

  it should "not create new project" in new Context {
    Given("inserted project id and request")
    val userId  = None
    val insertedProjectId = 1.asRight

    val request = CreateProjectRequest("Test project name")

    And("a service will not create project")
    val createProject = serviceUnderTest(userId, insertedProjectId)

    When("creating project where user does not exist")
    val result = createProject(request.projectName, "dsaddas32ndsjkn").unsafeRunSync()

    Then("returns user not found error")
    result shouldBe Left(UserNotFound)
  }

  it should "not create new project due to problem with insert" in new Context {
    Given("project error and userId")
    val userId  = Some(1)
    val insertedProjectId = ProjectNotCreated.asLeft

    val request = CreateProjectRequest("Test project name")

    And("a service will not create project and return its id")
    val createProject = serviceUnderTest(userId, insertedProjectId)

    When("creating project")
    val result = createProject(request.projectName, "dsaddas32ndsjkn").unsafeRunSync()

    Then("returns project not created error")
    result shouldBe Left(ProjectNotCreated)
  }

  private trait Context {

    def serviceUnderTest(
                          userId: Option[Int],
                          insertedProjectId: Either[LogTimeAppError, Int]
                        ): CreateProject[IO] = {

      val getUserId =  new GetUserByUUID[IO](null) {
        override def apply(userIdentification: String): IO[Option[Int]] = userId.pure[IO]
      }
      val createProject = new InsertProject[IO](null) {
        override def apply(projectName: String, userId: Long): IO[Either[LogTimeAppError, Int]] = insertedProjectId.pure[IO]

      }

      new CreateProject[IO](getUserId, createProject)
    }
  }
}
