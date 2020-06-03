package service.report

import cats.data.EitherT
import cats.effect.{IO, Sync}
import models.responses.{FinalProjectReport, Tasks}
import errorMessages._
import models.model.{Project, Task}
import repository.project.FindProjectByName
import repository.task.GetProjectTasks

class ProjectReport[F[+_] : Sync](
                                    findProject: FindProjectByName[F],
                                    tasks: GetProjectTasks[F]
                                              ) {

  def apply(projectName: String): F[Either[AppBusinessError, FinalProjectReport]] =
    (for {
      project <- findProjectById(projectName)
      projectTasks <- fetchTasksForProject(project.id)
    } yield {
      val totalDuration = projectTasks.map(_.duration).sum
      FinalProjectReport(project, Tasks(projectTasks), totalDuration)
    }).value


  private def findProjectById(projectName: String): EitherT[F, AppBusinessError, Project] = {
    EitherT(findProject(projectName))
  }

  private def fetchTasksForProject(id: Int): EitherT[F, AppBusinessError, List[Task]] = {
    EitherT(tasks(id))
  }
}
