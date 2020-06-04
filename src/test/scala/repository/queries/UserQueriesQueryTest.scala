package repository.queries

import repository.query._

class UserQueriesQueryTest extends QueryTest {

  test("User create should check") {
    check(UserQueries.insertUser("ussiddsdnjnsdwweq2322"))
  }

  test("User should retrieve user") {
    check(UserQueries.getUserIdByUUID("sdasdamsdaksdm"))
  }

  test("Task should fetch correctly by project id") {
    check(TaskQueries.fetchTasksForProject(2))
  }

  test("User fetch by id") {
    check(UserQueries.getUserById(2))
  }
}