package models

import java.time.{LocalDateTime, ZoneOffset, ZonedDateTime}

import cats.effect.IO
import models.model.{Ascending, ByCreatedTime}
import doobie.util.transactor.Transactor
import org.specs2.matcher.Matchers
import util.JsonSupport
import doobie._
import doobie.util.ExecutionContexts
import org.scalatest.funsuite.AnyFunSuite
import doobie.implicits._
import models.request.ReportRequest
import repository.queries.{Project, Report, Task, User}


class QueriesCheckSpec extends AnyFunSuite with Matchers with doobie.scalatest.IOChecker with JsonSupport {

  implicit val cs = IO.contextShift(ExecutionContexts.synchronous)
  val transactor = Transactor.fromDriverManager[IO](
    "org.postgresql.Driver",
    "jdbc:postgresql://ec2-54-75-229-28.eu-west-1.compute.amazonaws.com:5432/d94gigncif0u25",
    "vqidaoxnepgktr",
    "5075aa01fcdf2e9f371b817a92621d5da74acc7a655f3dd29585445f5fd4ffde"
  )


//  test("") {
//
//    val projectQuery = ReportRequest(Some(List("test1", "test2")), Some(ZonedDateTime.now(ZoneOffset.UTC).minusDays(10)), Some(ZonedDateTime.now(ZoneOffset.UTC).plusDays(10)), ByCreatedTime,Some(true), Ascending,0,0)
//
//    val query = Report.apply(projectQuery)
//
//    check(query)
//  }

  test("Project select should check") {
    check(Project.getProject("test"))
  }

  test("Project change name should check") {
    check(Project.changeName("oldName", "newName", 1))
  }

  test("Project delete should check") {
    check(Project.deleteProject(1, "test project", ZonedDateTime.now(ZoneOffset.UTC)))
  }

  test("Project insert should check") {
    check(Project.insert("testProjectData", 2))
  }

  test("User create should check") {
    check(User.insertUser("ussiddsdnjnsdwweq2322"))
  }

  test("User should retrieve user") {
    check(User.getUserId("sdasdamsdaksdm"))
  }

  test("Task should fetch correctly by project id") {
    check(Task.fetchTasksForProject(2))
  }

  test("User select last inserted") {
    check(User.selectLastInsertedUser())
  }

  test("User fetch by id") {
    check(User.selectByUserIdentity(2))
  }

}
