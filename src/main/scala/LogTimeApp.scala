import akka.actor._
import akka.http.scaladsl.Http
import scala.concurrent.{ExecutionContextExecutor, Future}
import pureconfig.ConfigSource
import pureconfig._
import pureconfig.generic.auto._
import com.typesafe.config.ConfigFactory

object LogTimeApp extends App with LogTimeService {

  implicit val system: ActorSystem = ActorSystem("LogTimeSystem")
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  val appConfig = ConfigFactory.load("application.conf")

  Http().bindAndHandle(routes, appConfig.getString("http.interface"), appConfig.getInt("http.port"))
}