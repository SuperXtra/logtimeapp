package models.model

sealed trait ProjectSort extends Product with Serializable
case object ByCreatedTime extends ProjectSort
case object ByUpdateTime extends ProjectSort