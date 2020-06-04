package service.report

import cats.data.EitherT
import cats.effect.{IO, Sync}
import models.reports.{FinalProjectReport, Tasks}
import error._
import models.model.{Project, Task}
import repository.project.GetProjectByName
import repository.task.GetProjectTasks

class GetProjectReport[F[+_] : Sync](
                                   findProject: GetProjectByName[F],
                                   tasks: GetProjectTasks[F]
                                              ) {

  def apply(projectName: String): F[Either[LogTimeAppError, FinalProjectReport]] =
    (for {
      project <- findProjectById(projectName)
      projectTasks <- fetchTasksForProject(project.id)
    } yield {
      val totalDuration = projectTasks.map(_.duration).sum
      FinalProjectReport(project, Tasks(projectTasks), totalDuration)
    }).value


  private def findProjectById(projectName: String): EitherT[F, LogTimeAppError, Project] = {
    EitherT.fromOptionF(findProject(projectName), ProjectNotFound)
  }

  private def fetchTasksForProject(id: Int): EitherT[F, LogTimeAppError, List[Task]] = {
    EitherT(tasks(id))
  }
}
