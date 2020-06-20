//package repository.queries
//
//import java.time._
//
//import models.{ProjectId, TaskDuration, UserId, Volume}
//import models.request.LogTaskRequest
//import repository.query.{TaskQueries, UserQueries}
//
//class TaskQueriesQueryTest extends QueryTest {
//
//  test("User create should check") {
//    check(TaskQueries.insert(LogTaskRequest(
//      "test name",
//      "test description",
//      ZonedDateTime.now(ZoneOffset.UTC),
//      TaskDuration(50),
//      Some(Volume(2)),
//      Some("test comment")), ProjectId(2), UserId(2), LocalDateTime.now()))
//  }
//
//  test("User delete test") {
//    check(TaskQueries.deleteTask("task", ProjectId(1), UserId(1), LocalDateTime.now()))
//  }
//
//  test("test if user exist with given uuid query") {
//    check(UserQueries.userExists("123sdaaksjdaskjd"))
//  }
//
//  test("Get user id by user uuid") {
//    check(UserQueries.getUserIdByUUID("asddadsa"))
//  }
//
//  test("Insert user with given uuid") {
//    check(UserQueries.insertUser("asdkjasklkdjasj"))
//  }
//
//  test("Select by user id") {
//    check(UserQueries.getUserById(UserId(1)))
//  }
//}