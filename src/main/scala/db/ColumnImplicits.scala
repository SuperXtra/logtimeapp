package db

import models.{Active, ProjectId, TaskDuration, TaskId, UserId, Volume}
import slick.ast.BaseTypedType
import slick.jdbc.PostgresProfile.api._

object ColumnImplicits {
  implicit val taskPKMapper: BaseTypedType[TaskId] =       MappedColumnType.base[TaskId, Int](_.value, TaskId)
  implicit val userPKMapper: BaseTypedType[UserId] =       MappedColumnType.base[UserId, Int](_.value, UserId)
  implicit val projectPKMapper: BaseTypedType[ProjectId] =    MappedColumnType.base[ProjectId, Int](_.value, ProjectId)
  implicit val activeMapper: BaseTypedType[Active] =       MappedColumnType.base[Active, Boolean](_.value, Active)
  implicit val taskDurationMapper:  BaseTypedType[TaskDuration] = MappedColumnType.base[TaskDuration, Int](_.value, TaskDuration)
  implicit val volumeMapper: BaseTypedType[Volume] =       MappedColumnType.base[Volume, Int](_.value, Volume)
}
