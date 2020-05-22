import dbConnection.PostgresDb
import akka.http.scaladsl.model.DateTime
import java.util.UUID
import service.UserService

object WebApp extends App {


  val service = new UserService()



  println("running")
//  val result = PostgresDb.test("54106ee8-a3ac-4dd6-834e-db8652e99068")
  service.createNewUser().map {
    case Left(value) => println(value)
    case Right(value) => println(value)
  }.unsafeRunSync()

//  service.checkIfExists("76e73815-4fd1-4576-9942-acefe9a01fff").map {
//    case Left(value) => print(value)
//    case Right(value) => value.size match {
//      case x if x == 1 => println(true)
//      case _ => println(false)
//    }
//  }.unsafeRunSync()
//
//
//  service.checkIfExists("76e73815-4fd1-4576-9942-").map {
//    case Left(value) => print(value)
//    case Right(value) => value.size match {
//      case x if x == 1 => println(true)
//      case _ => println(false)
//    }
//  }.unsafeRunSync()

  println(DateTime.now)

//    .update.run.transact(xa).unsafeRunSync()

//  implicit val system = ActorSystem("projectAppSystem")
//  implicit val materializer = ActorMaterializer()
//  println(s)


}
