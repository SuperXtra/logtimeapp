package repository.queries

import java.time._

import models.UserId
import repository.query.ProjectQueries

class ProjectQueriesQueryTest extends QueryTest {

  test("Project select should check") {
    check(ProjectQueries.getActiveProjectByName("test"))
  }

  test("Project change name should check") {
    check(ProjectQueries.changeName("oldName", "newName", UserId(1)))
  }

  test("Project delete should check") {
    check(ProjectQueries.deactivate(UserId(1), "test project", LocalDateTime.now()))
  }

  test("Project insert should check") {
    check(ProjectQueries.insert("testProjectData", UserId(2)))
  }
}