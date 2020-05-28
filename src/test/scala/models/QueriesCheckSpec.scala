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
import repository.queries.{Project, Report}


class QueriesCheckSpec extends AnyFunSuite with Matchers with doobie.scalatest.IOChecker with JsonSupport {

  implicit val cs = IO.contextShift(ExecutionContexts.synchronous)
//  project_name, create_time, delete_time,
//  user_id, task_description, start_time, end_time, duration, volume, comment, delete_time




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
//
//  test("Project select should check") {
//    check(Project.getProject("test"))
//  }
//
//  test("Project change name should check") {
//    check(Project.changeName("oldName", "newName", 1))
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
