package data

object Entities {

  case class User(
                   id: Int,
                   userIdentification: String
                 )

  case class Task(
                   id: Int,
                   projectId: Int,
                   userId: Int,
                   taskDescription: String,
                   startTime: String,
                   endTime: Option[String],
                   volume: Option[Int],
                   comment: Option[String],
                   deleteTime: Option[String],
                   active: Boolean
                 )

  case class Project(
                      id: Int,
                      userId: Int,
                      projectName: String,
                      createTime: String,
                      deleteTime: Option[String],
                      active: Boolean
                    )


}


