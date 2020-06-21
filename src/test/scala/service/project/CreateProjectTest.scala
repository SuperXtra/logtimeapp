package service.project

import akka.event.{MarkerLoggingAdapter, NoMarkerLogging}
import cats.effect.{ContextShift, IO}
import error.{LogTimeAppError, ProjectNotCreated, UserNotFound}
import models.model.User
import org.scalatest.GivenWhenThen
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import repository.project.InsertProject
import repository.user._
import cats.implicits._
import com.typesafe.config.{Config, ConfigFactory}
import config.DatabaseConfig
import db.DatabaseContext
import doobie.util.ExecutionContexts
import models.{ProjectId, UserId}
import models.request._
import pureconfig.ConfigSource
import service.SetUp
import slick.dbio._

class CreateProjectTest extends AnyFlatSpec with Matchers with GivenWhenThen {

  it should "create new project" in new Context {
    Given("user id, inserted project id and request")
    val user  = User(UserId(2), "123").asRight
    val insertedProjectId = ProjectId(4).asRight
    val request = CreateProjectRequest("Test project name")

    And("a service will create project and return its id")
    val createProject = serviceUnderTest(user, insertedProjectId)

    When("creating project")
    val result = createProject(request.projectName, "dsaddas32ndsjkn").unsafeRunSync()

    Then("returns project id")
    result shouldBe Right(ProjectId(4))
  }

  it should "not create new project" in new Context {
    Given("inserted project id and request")
    val user  = UserNotFound.asLeft
    val insertedProjectId = ProjectId(1).asRight

    val request = CreateProjectRequest("Test project name")

    And("a service will not create project")
    val createProject = serviceUnderTest(user, insertedProjectId)

    When("creating project where user does not exist")
    val result = createProject(request.projectName, "dsaddas32ndsjkn").unsafeRunSync()

    Then("returns user not found error")
    result shouldBe Left(UserNotFound)
  }

  it should "not create new project due to problem with insert" in new Context {
    Given("project error and userId")
    val user  = ProjectNotCreated.asLeft
    val insertedProjectId = ProjectNotCreated.asLeft

    val request = CreateProjectRequest("Test project name")

    And("a service will not create project and return its id")
    val createProject = serviceUnderTest(user, insertedProjectId)

    When("creating project")
    val result = createProject(request.projectName, "dsaddas32ndsjkn").unsafeRunSync()

    Then("returns project not created error")
    result shouldBe Left(ProjectNotCreated)
  }

  private trait Context extends SetUp {

    def serviceUnderTest(
                          userId: Either[LogTimeAppError, User],
                          insertedProjectId: Either[LogTimeAppError, ProjectId]
                        ): CreateProject[IO] = {

      val getUserId =  new GetUserByUUID[IO] {
        override def apply(userIdentification: String) = DBIOAction.successful(userId)
      }
      val createProject = new InsertProject[IO] {
        override def apply(projectName: String, userId: UserId) = DBIOAction.successful(insertedProjectId)
      }

      new CreateProject[IO](getUserId, createProject)
    }
  }
}
