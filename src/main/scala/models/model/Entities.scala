package models.model

import java.time._

case class UserTb(
                   id: Int,
                   userIdentification: String
                 )

case class TaskTb(
                   id: Int,
                   projectId: Int,
                   userId: Int,
                   createTime: LocalDateTime,
                   taskDescription: String,
                   startTime: LocalDateTime,
                   endTime: LocalDateTime,
                   duration: Long,
                   volume: Option[Int],
                   comment: Option[String],
                   deleteTime: Option[LocalDateTime],
                   active: Boolean
                 )

case class ProjectTb(
                      id: Int,
                      userId: Int,
                      projectName: String,
                      createTime: LocalDateTime,
                      deleteTime: Option[LocalDateTime],
                      active: Option[Boolean]
                    )