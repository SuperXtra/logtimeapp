package models.model

import java.time._

case class User(
                   id: Int,
                   userIdentification: String
                 )

case class Task(
                   id: Int,
                   projectId: Int,
                   userId: Int,
                   createTime: LocalDateTime,
                   taskDescription: String,
                   startTime: LocalDateTime,
                   endTime: LocalDateTime,
                   duration: Int,
                   volume: Option[Int],
                   comment: Option[String],
                   deleteTime: Option[LocalDateTime],
                   active: Option[Boolean]
                 )

case class Project(
                      id: Int,
                      userId: Int,
                      projectName: String,
                      createTime: LocalDateTime,
                      deleteTime: Option[LocalDateTime],
                      active: Option[Boolean]
                    )