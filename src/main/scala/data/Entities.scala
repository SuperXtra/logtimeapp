package data

import java.time.{LocalDateTime, ZonedDateTime}

import org.joda.time.DateTime


object Entities {

  case class User(
                   id: Long,
                   userIdentification: String
                 )

  case class Task(
                   id: Long,
                   projectId: Long,
                   userId: Long,
                   taskDescription: String,
                   startTime: LocalDateTime,
                   endTime: LocalDateTime,
                   duration: Long,
                   volume: Option[Int],
                   comment: Option[String],
                   deleteTime: Option[LocalDateTime],
                   active: Boolean
                 )

  case class Project(
                      id: Long,
                      userId: Long,
                      projectName: String,
                      createTime: LocalDateTime,
                      deleteTime: Option[LocalDateTime],
                      active: Boolean
                    )

}



