package service.report

import cats.data.EitherT
import cats.effect.{IO, Sync}
import models.responses.{GeneralReport, Tasks}
import errorMessages._
import models.model.{Project, Task}
import repository.project.FindActiveProjectById
import repository.task.GetProjectTasks

class OverviewReport[F[+_] : Sync](
                                    findProject: FindActiveProjectById[F],
                                    tasks: GetProjectTasks[F]
                                              ) {

  def apply(projectName: String): F[Either[AppBusinessError, GeneralReport]] =
    (for {
      project <- findProjectById(projectName)
      projectTasks <- fetchTasksForProject(project.id)
    } yield {
      val totalDuration = projectTasks.map(_.duration).sum
      GeneralReport(project, Tasks(projectTasks), totalDuration)
    }).value


  private def findProjectById(projectName: String): EitherT[F, AppBusinessError, Project] = {
    EitherT.fromOptionF(findProject(projectName), ProjectNotFound())
  }

  private def fetchTasksForProject(id: Int): EitherT[F, AppBusinessError, List[Task]] = {
    EitherT(tasks(id))
  }
}
