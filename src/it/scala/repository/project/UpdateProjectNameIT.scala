//package repository.project
//
//import java.util.UUID
//
//import cats.effect.{ContextShift, IO}
//import com.dimafeng.testcontainers.{ForAllTestContainer, PostgreSQLContainer}
//import db.InitializeDatabase
//import doobie.util.ExecutionContexts
//import doobie.util.transactor.Transactor
//import error.{ProjectNotCreated, ProjectUpdateUnsuccessful}
//import org.scalatest.{BeforeAndAfterEach, GivenWhenThen}
//import org.scalatest.flatspec.AnyFlatSpec
//import org.scalatest.matchers.should.Matchers
//import repository.user.InsertUser
//
//class UpdateProjectNameIT extends AnyFlatSpec with Matchers with GivenWhenThen with ForAllTestContainer with BeforeAndAfterEach {
//
//  override val container = new PostgreSQLContainer()
//
//  it should "update existing project id" in new Context {
//
//    Given("existing user")
//    val userId = createUser(UUID.randomUUID().toString).unsafeRunSync().right.get
//
//    And("existing project")
//    val projectName = "test_project"
//    insertProject(projectName, userId).unsafeRunSync()
//
//    When("updating existing project")
//    val newName = projectName + "test"
//    update(projectName, newName, userId).unsafeRunSync()
//
//    And("fetching active project by name")
//    val result = findActiveProjectByName(newName).unsafeRunSync.get
//
//    Then("it should return new project name")
//    result.projectName shouldBe newName
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
//    val findActiveProjectByName = new GetProjectByName[IO](tx)
//    val insertProject = new InsertProject(tx)
//    val createUser = new InsertUser[IO](tx)
//    val update = new UpdateProjectName[IO](tx)
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