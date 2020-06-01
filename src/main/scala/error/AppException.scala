package error

case class AppException(
                         error: String // error.project.not.found
                        // code: AppExceptionCode
                       )


// TODO: consider AppExceptionCode
// sealed abstract class ProjectNotFound
// object ProjectNotFound {
//    case object ProjectNotFound extends AppExceptionCode
// }

// AppExceptionCode.ProjectNotFound