package service.report

import akka.event.MarkerLoggingAdapter
import cats.effect.{ContextShift, IO, Sync}
import models.reports.{FinalProjectReport, Tasks}
import error._
import models.{ProjectId, WorkedTime}
import repository.project.GetProjectByName
import repository.task.GetProjectTasks
import utils.EitherT
import slick.jdbc.PostgresProfile.api._
import scala.concurrent._
import ExecutionContext.Implicits.global
import db.RunDBIOAction._

class GetProjectReport[F[+_] : Sync](
                                   findProject: GetProjectByName[F],
                                   tasks: GetProjectTasks[F]
                                              )
                                    (implicit db: Database,
                                     logger: MarkerLoggingAdapter,
                                     ec: ContextShift[IO])  {

  def apply(projectName: String): IO[Either[LogTimeAppError, FinalProjectReport]] =
    (for {
      project <- findProjectById(projectName)
      projectTasks <- fetchTasksForProject(project.id)
      _ = logging.generatingProjectReport(projectName)
    } yield {
      val totalDuration = projectTasks.map(_.duration.value).sum
      FinalProjectReport(project, Tasks(projectTasks), WorkedTime(totalDuration))
    }).value.exec


  private def findProjectById(projectName: String) = {
    EitherT(findProject(projectName))
  }

  private def fetchTasksForProject(projectId: ProjectId)= {
    EitherT(tasks(projectId))
  }
}
