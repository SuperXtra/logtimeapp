package repository.queries

import java.time.{ZoneOffset, ZonedDateTime}

import repository.query.ProjectQueries

class ProjectQueriesQueryTest extends QueryTest {

  test("Project select should check") {
    check(ProjectQueries.getActiveProjectById("test"))
  }

  test("Project change name should check") {
    check(ProjectQueries.changeName("oldName", "newName", 1))
  }

  test("Project delete should check") {
    check(ProjectQueries.deleteProject(1, "test project", ZonedDateTime.now(ZoneOffset.UTC)))
  }

  test("Project insert should check") {
    check(ProjectQueries.insert("testProjectData", 2))
  }

}
