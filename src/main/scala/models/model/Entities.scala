package models.model

import java.time._

case class UserTb(
                   id: Long,
                   userIdentification: String
                 )

case class TaskTb(
                   id: Long,
                   projectId: Long,
                   userId: Long,
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
                      id: Long,
                      userId: Long,
                      projectName: String,
                      createTime: LocalDateTime,
                      deleteTime: Option[LocalDateTime],
                      active: Boolean
                    )