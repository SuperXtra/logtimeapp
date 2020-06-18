package service.project

import akka.event.MarkerLoggingAdapter
import cats.implicits._
import cats.effect._

object logging {

  // TODO: Create logging object in eah package inside services
  def projectCreated[F[_]: Sync](projectId: Int, projectName: String, userId: Int)
                                (implicit logger: MarkerLoggingAdapter): Unit =
    {
      logger.info(s"User with id: ${userId} created new project with id: ${projectId} and name: ${projectName}")
      println("project created logger")
    }
}