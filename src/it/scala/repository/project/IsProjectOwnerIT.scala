//package repository.project
//
//import java.util.UUID
//
//import cats.effect.{Clock, ContextShift, IO}
//import com.dimafeng.testcontainers.{ForAllTestContainer, PostgreSQLContainer}
//import db.InitializeDatabase
//import doobie.util.ExecutionContexts
//import doobie.util.transactor.Transactor
//import error.{LogTimeAppError, ProjectDeleteUnsuccessfulUserIsNotTheOwner, ProjectNotFound}
//import models.IsOwner
//import org.scalatest.{BeforeAndAfterEach, GivenWhenThen}
//import org.scalatest.flatspec.AnyFlatSpec
//import org.scalatest.matchers.should.Matchers
//import pureconfig.ConfigSource
//import repository.user.InsertUser
//
//class IsProjectOwnerIT extends AnyFlatSpec with Matchers with GivenWhenThen with ForAllTestContainer with BeforeAndAfterEach {
//
//  override val container = new PostgreSQLContainer()
//
//  it should "check if provided user it the owner of the project" in new Context {
//
//    Given("existing user")
//    val userId = createUser(UUID.randomUUID().toString).unsafeRunSync().right.get
//
//    And("existing project")
//    val projectName = "test_project"
//    insertProject(projectName, userId).unsafeRunSync()
//
//    And("a data access function able of checking if user is the owner")
//    val checkIfIsOwner = new IsProjectOwner[IO](tx)
//
//    When("fetching active project by name")
//    val result = checkIfIsOwner(userId, projectName).unsafeRunSync
//
//    Then("it should return true")
//    result shouldBe Right(IsOwner(true))
//  }
//
//  it should "check if provided user is not the owner of the project" in new Context {
//
//    Given("existing user - owner of the project")
//    val userId = createUser(UUID.randomUUID().toString).unsafeRunSync().right.get
//
//    Given("existing user - who is not the creator of the project")
//    val OtherUserId = createUser(UUID.randomUUID().toString).unsafeRunSync().right.get
//
//    And("existing project")
//    val projectName = "test_project"
//    insertProject(projectName, userId).unsafeRunSync()
//
//    And("a data access function able of checking if user is the owner")
//    val checkIfIsOwner = new IsProjectOwner[IO](tx)
//
//    When("fetching active project by name")
//    val result = checkIfIsOwner(OtherUserId, projectName).unsafeRunSync
//
//    Then("it should return project delete unsuccessful user is not the owner error")
//    result shouldBe Left(ProjectDeleteUnsuccessfulUserIsNotTheOwner)
//  }
//
//  private trait Context {
//
//    implicit val cs: ContextShift[IO] = IO.contextShift(ExecutionContexts.synchronous)
//
//    val tx = Transactor.fromDriverManager[IO](
//      container.driverClassName,
//      container.jdbcUrl,
//      container.username,
//      container.password
//    )
//
//    val insertProject = new InsertProject(tx)
//    val createUser = new InsertUser[IO](tx)
//
//    import doobie.implicits._
//
//    (for {
//      _ <- sql"DELETE from tb_project".update.run
//      _ <- sql"DELETE from tb_user".update.run
//      _ <- sql"DELETE from tb_task".update.run
//    } yield ()).transact(tx).unsafeRunSync()
//  }
//
//  override def beforeEach(): Unit = {
//    container.start()
//    new InitializeDatabase[IO].apply(
//      container.jdbcUrl,
//      container.username,
//      container.password
//    ).unsafeRunSync()
//  }
//}
