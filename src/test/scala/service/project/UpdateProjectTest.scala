//package service.project
//
//import java.time.LocalDateTime
//
//import akka.event.{MarkerLoggingAdapter, NoMarkerLogging}
//import cats.effect.IO
//import models.model.{Project, User}
//import org.scalatest.GivenWhenThen
//import org.scalatest.flatspec.AnyFlatSpec
//import org.scalatest.matchers.should.Matchers
//import repository.project.{GetProjectByName, UpdateProjectName}
//import repository.user._
//import cats.implicits._
//import error._
//import models.{Active, ProjectId, UserId}
//import models.request.{ChangeProjectNameRequest, DeleteProjectRequest}
//
//class UpdateProjectTest extends AnyFlatSpec with Matchers with GivenWhenThen {
//
//  it should "update project" in new Context {
//    Given("user id, result of updating project, updated project")
//    val userId = Some(UserId(1))
//    val updateProjectName = "After change"
//    val updatedProject = Project(ProjectId(1), UserId(1), updateProjectName, LocalDateTime.now(), Some(LocalDateTime.now().plusHours(2)), Some(Active(true)))
//
//    And("ability to check if project was updated")
//    var newName = none[String]
//    var oldName = none[String]
//    val updateName = (on: String, nn: String) => IO{
//      oldName = on.some
//      newName = nn.some
//      ().asRight[LogTimeAppError]
//    }
//
//    And("a service will update project")
//    val updateProject = serviceUnderTest(userId, updateName)
//
//    val changeProjectName = ChangeProjectNameRequest(
//      oldProjectName = "Test project name",
//      projectName = updateProjectName
//    )
//
//    When("updating project")
//    val result = updateProject(changeProjectName.oldProjectName, changeProjectName.projectName, "uudissdsa2321hjd8fs").unsafeRunSync()
//
//    Then("returns project")
//    result.isRight shouldBe true
//
//    And("correct old name was passed")
//    oldName shouldBe changeProjectName.oldProjectName.some
//
//    And("correct new name was passed")
//    newName shouldBe changeProjectName.projectName.some
//
//  }
//
//  it should "not update project" in new Context {
//    Given("user id, result of updating project, updated project")
//    val userId = Some(UserId(1))
//    val updateName = (_: String,_: String) => IO{
//      ProjectNotCreated.asLeft[Unit]
//    }
//
//    And("a service will update project")
//    val updateProject = serviceUnderTest(userId, updateName)
//
//    val changeProjectName = ChangeProjectNameRequest(
//      oldProjectName = "Test project name",
//      projectName = "After change"
//    )
//
//    When("updating project")
//    val result = updateProject(changeProjectName.oldProjectName,changeProjectName.projectName, "uudissdsa2321hjd8fs").unsafeRunSync()
//
//    Then("returns project not created error")
//    result shouldBe Left(ProjectNotCreated)
//  }
//
//  private trait Context {
//    implicit lazy val logger: MarkerLoggingAdapter = NoMarkerLogging
//
//    def serviceUnderTest(
//                          userId: Option[UserId],
//                          updateName: (String, String) => IO[Either[LogTimeAppError, Unit]]
//                        ): UpdateProject[IO] = {
//
//      val user = new GetUserByUUID[IO](null) {
//        override def apply(userIdentification: String): IO[Option[UserId]] = userId.pure[IO]
//      }
//      val updateProjectName = new UpdateProjectName[IO](null) {
//        override def apply(oldName: String, newName: String, userId: UserId): IO[Either[LogTimeAppError, Unit]] = updateName(oldName, newName)
//      }
//
//      new UpdateProject[IO](user, updateProjectName)
//    }
//  }
//}