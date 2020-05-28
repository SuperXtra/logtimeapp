package service.report

import cats.data.EitherT
import cats.effect.{IO, Sync}
import models.responses.{ProjectReport, Tasks}
import error._
import models.model.{ProjectTb, TaskTb}
import repository.project.FindProjectById
import repository.task.GetProjectTasks

class ProjectTasksDurationReport[F[+_] : Sync](
                                                findProject: FindProjectById[F],
                                                tasks: GetProjectTasks[F]
                                              ) {

  def apply(projectName: String): F[Either[AppError, ProjectReport]] =
    (for {
      project <- findProjectById(projectName)
      projectTasks <- fetchTasksForProject(project.id)
    } yield {
      val totalDuration = projectTasks.map(_.duration).sum
      ProjectReport(project, Tasks(projectTasks), totalDuration)
    }).value


  private def findProjectById(projectName: String): EitherT[F, AppError, ProjectTb] = {
    EitherT.fromOptionF(findProject(projectName), ProjectNotCreated())
  }

  private def fetchTasksForProject(id: Int): EitherT[F, AppError, List[TaskTb]] = {
    EitherT(tasks(id))
  }
}
