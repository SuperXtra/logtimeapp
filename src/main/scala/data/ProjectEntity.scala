package data

case class ProjectEntity(
                        id: Int,
                        userId: Int,
                        projectName: String,
                        creatTime: String,
                        deleteTime: Option[String]
                        )
