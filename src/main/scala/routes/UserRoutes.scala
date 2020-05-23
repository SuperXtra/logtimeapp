package routes

import akka.actor.ActorSystem
import akka.http.scaladsl.server.Route
import service.UserService
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.MethodDirectives.get
import akka.http.scaladsl.server.directives.PathDirectives.path
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import akka.http.scaladsl.server.Directives.{as, concat, entity, pathEndOrSingleSlash, pathPrefix, put, rejectEmptyResponse, _}
import scala.concurrent.{ExecutionContext, Future}

object UserRoutes {

//  def routes(service: UserService)(implicit ec: ExecutionContext, system: ActorSystem): Route = {

//    val route =
//      pathPrefix("api" / "v1" / "user") {
//        pathEnd {
//          post {
//            rejectEmptyResponse {
//
//              onSuccess(service.createNewUser()){
//
//              }
//            }
//          }
//        }
//      }
//
//  }

}
