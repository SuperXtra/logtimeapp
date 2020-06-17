package models.model

sealed trait SortDirection  extends Product with Serializable
case object Ascending extends SortDirection
case object Descending extends SortDirection