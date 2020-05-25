package data

import java.time.ZonedDateTime

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
                   startTime: ZonedDateTime,
                   endTime: ZonedDateTime,
                   duration: Long,
                   volume: Option[Int],
                   comment: Option[String],
                   deleteTime: Option[String],
                   active: Boolean
                 )

  case class Project(
                      id: Long,
                      userId: Long,
                      projectName: String,
                      createTime: ZonedDateTime,
                      deleteTime: Option[ZonedDateTime],
                      active: Boolean
                    )


}



