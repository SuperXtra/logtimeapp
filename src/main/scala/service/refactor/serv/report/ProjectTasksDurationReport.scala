package service.refactor.serv.report

import cats.data.EitherT
import cats.effect.{IO, Sync}
import data.Entities.{Project, Task}
import data.{ProjectReport, Queries, Tasks}
import doobie.util.transactor.Transactor
import error.{AppError, FetchingTaskForProjectUnsuccessful, ProjectNotCreated}
import service.refactor.repo.project.{FindProjectById, UpdateProjectName}
import service.refactor.repo.task.GetProjectTasks
import service.refactor.repo.user.GetExistingUserId

class ProjectTasksDurationReport[F[+_] : Sync](
                                                findProject: FindProjectById[F],
                                                tasks: GetProjectTasks[F]
                                              ) {

  def tasksAndDuration(projectName: String): F[Either[AppError, ProjectReport]] =
    (for {
      project <- findProjectById(projectName)
      projectTasks <- fetchTasksForProject(project.id)
    } yield {
      //      val totalDuration = projectTasks match {
      //        case _ :: _ => projectTasks.map(_.duration).sum
      //        case Nil => 0
      //      }
      val totalDuration = projectTasks.map(_.duration).sum
      ProjectReport(project, Tasks(projectTasks), totalDuration)
    }).value


  private def findProjectById(projectName: String): EitherT[F, AppError, Project] = {
    EitherT.fromOptionF(findProject(projectName), ProjectNotCreated)
  }

  private def fetchTasksForProject(id: Long): EitherT[F, AppError, List[Task]] = {
    EitherT(tasks(id))
  }
}
