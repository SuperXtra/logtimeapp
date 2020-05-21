package data

object Entity {
  case class User(
                         id: Int,

                       )

  case class Task(
                         id: Int,
                         projectId: Int,
                         userId: Int,
                         taskDescription: String,
                         startTime: String,
                         endTime: String,
                         volume: Int,
                         comment: String,
                         deleteTime: Option[String]
                       )

  case class Project(
                            id: Int,
                            userId: Int,
                            projectName: String,
                            creatTime: String,
                            deleteTime: Option[String]
                          )

}
