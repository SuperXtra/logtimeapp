package data

import cats.effect.IO
import doobie.util.transactor.Transactor
import org.specs2.matcher.Matchers
//import doobie._
import doobie.util.ExecutionContexts
import org.scalatest.funsuite.AnyFunSuite

class QueriesCheckSpec extends AnyFunSuite with Matchers with doobie.scalatest.IOChecker {

  implicit val cs = IO.contextShift(ExecutionContexts.synchronous)

  val transactor = Transactor.fromDriverManager[IO](
    "org.postgresql.Driver",
    "jdbc:postgresql:test",
    "test",
    "test"
  )

//  test("Project select should check") {
//    check(Queries.Project.getProject("test"))
//  }
//
//  test("Project change name should check") {
//    check(Queries.Project.changeName("oldName", "newName", 1))
//  }

//  test("Project delete should check") {
//    check(Queries.Project.deleteProject(1, "test project"))
//  }

//  test("Project insert should check") {
//    check(Queries.Project.insert("testProjectData", 2))
//  }
//
//  test("User create should check") {
//    check(Queries.User.insertUser("ussiddsdnjnsdwweq2322"))
//  }
//  test("User create with null check") {
//    check(Queries.User.insertUser(null))
//  }
//
//  test("User should retrieve user") {
//    check(Queries.User.getUserId("sdasdamsdaksdm"))
//  }

//  test("Task should fetch correctly by task description and project id") {
//    check(Queries.Task.fetchTasksForProject("test task description", 2))
//  }

//  test("User select last inserted") {
//    check(Queries.User.selectLastInsertedUser())
//  }
//
//  test("User fetch by id") {
//    check(Queries.User.selectByUserIdentity(2))
//  }

}
