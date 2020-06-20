//package repository.queries
//
//import models.{ProjectId, UserId}
//import repository.query._
//
//class UserQueriesQueryTest extends QueryTest {
//
//  test("User create should check") {
//    check(UserQueries.insertUser("ussiddsdnjnsdwweq2322"))
//  }
//
//  test("User should retrieve user") {
//    check(UserQueries.getUserIdByUUID("sdasdamsdaksdm"))
//  }
//
//  test("Task should fetch correctly by project id") {
//    check(TaskQueries.fetchTasksForProject(ProjectId(2)))
//  }
//
//  test("User fetch by id") {
//    check(UserQueries.getUserById(UserId(2)))
//  }
//}