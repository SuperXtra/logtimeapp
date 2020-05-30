package repository.queries

import repository.query.{TaskQueries, UserQueries}

class UserQueriesQueryTest extends QueryTest {

  test("User create should check") {
    check(UserQueries.insertUser("ussiddsdnjnsdwweq2322"))
  }

  test("User should retrieve user") {
    check(UserQueries.getUserId("sdasdamsdaksdm"))
  }

  test("Task should fetch correctly by project id") {
    check(TaskQueries.fetchTasksForProject(2))
  }

  test("User select last inserted") {
    check(UserQueries.selectLastInsertedUser())
  }

  test("User fetch by id") {
    check(UserQueries.selectByUserIdentity(2))
  }
}
