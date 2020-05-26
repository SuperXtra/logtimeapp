package error

// TODO make from it universal error with marshaller that can be returned by every router
case class AppException(errorMessage: String) extends Exception(errorMessage)

object AppException {
  import io.circe._
  import io.circe.generic.semiauto._
  implicit val appExceptionEncoder: Encoder.AsObject[AppException] = deriveEncoder
}