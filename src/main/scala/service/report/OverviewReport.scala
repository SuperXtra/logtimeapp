package service.report

import cats.data.EitherT
import cats.effect.{IO, Sync}
import models.responses.{GeneralReport, Tasks}
import error._
import models.model.{Project, Task}
import repository.project.FindProjectById
import repository.task.GetProjectTasks

class OverviewReport[F[+_] : Sync](
                                                findProject: FindProjectById[F],
                                                tasks: GetProjectTasks[F]
                                              ) {

  def apply(projectName: String): F[Either[AppError, GeneralReport]] =
    (for {
      project <- findProjectById(projectName)
      projectTasks <- fetchTasksForProject(project.id)
    } yield {
      val totalDuration = projectTasks.map(_.duration).sum
      GeneralReport(project, Tasks(projectTasks), totalDuration)
    }).value


  private def findProjectById(projectName: String): EitherT[F, AppError, Project] = {
    EitherT.fromOptionF(findProject(projectName), ProjectNotCreated())
  }

  private def fetchTasksForProject(id: Int): EitherT[F, AppError, List[Task]] = {
    EitherT(tasks(id))
  }
}
