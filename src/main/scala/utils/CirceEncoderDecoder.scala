package utils

import io.circe.{Decoder, Encoder}
import io.circe.syntax._
import io.circe.generic.extras.semiauto._
import io.circe.generic.extras.defaults._
import models._

object CirceEncoderDecoder {


  //Encoders
  implicit val UserIdEncoder:       Encoder[UserId]           = deriveUnwrappedEncoder
  implicit val TaskIdEncoder:       Encoder[TaskId]           = deriveUnwrappedEncoder
  implicit val TaskDurationEncoder: Encoder[TaskDuration]     = deriveUnwrappedEncoder
  implicit val VolumeEncoder:       Encoder[Volume]           = deriveUnwrappedEncoder
  implicit val ActiveEncoder:       Encoder[Active]           = deriveUnwrappedEncoder
  implicit val ProjectIdEncoder:    Encoder[ProjectId]        = deriveUnwrappedEncoder
  implicit val PageEncoder:         Encoder[Page]             = deriveUnwrappedEncoder
  implicit val QuantityEncoder:     Encoder[Quantity]         = deriveUnwrappedEncoder
  implicit val YearEncoder:         Encoder[Year]             = deriveUnwrappedEncoder
  implicit val MonthEncoder:        Encoder[Month]            = deriveUnwrappedEncoder
  implicit val WorkedTimeEncoder:   Encoder[WorkedTime]       = deriveUnwrappedEncoder
  implicit val TotalCountEncoder:   Encoder[TotalCount]       = deriveUnwrappedEncoder
  implicit val IsOwnerEncoder:      Encoder[IsOwner]          = deriveUnwrappedEncoder
  implicit val DeleteCountEncoder:  Encoder[DeleteCount]      = deriveUnwrappedEncoder
  implicit val ExistsEncoder:       Encoder[Exists]           = deriveUnwrappedEncoder

  //Decoders
  implicit val UserIdDecoder:       Decoder[UserId]           = deriveUnwrappedDecoder
  implicit val Decoder:             Decoder[TaskId]           = deriveUnwrappedDecoder
  implicit val TaskDurationDecoder: Decoder[TaskDuration]     = deriveUnwrappedDecoder
  implicit val VolumeDecoder:       Decoder[Volume]           = deriveUnwrappedDecoder
  implicit val ActiveDecoder:       Decoder[Active]           = deriveUnwrappedDecoder
  implicit val ProjectIdDecoder:    Decoder[ProjectId]        = deriveUnwrappedDecoder
  implicit val PageDecoder:         Decoder[Page]             = deriveUnwrappedDecoder
  implicit val QuantityDecoder:     Decoder[Quantity]         = deriveUnwrappedDecoder
  implicit val YearDecoder:         Decoder[Year]             = deriveUnwrappedDecoder
  implicit val MonthDecoder:        Decoder[Month]            = deriveUnwrappedDecoder
  implicit val WorkedTimeDecoder:   Decoder[WorkedTime]       = deriveUnwrappedDecoder
  implicit val TotalCountDecoder:   Decoder[TotalCount]       = deriveUnwrappedDecoder
  implicit val IsOwnerDecoder:      Decoder[IsOwner]          = deriveUnwrappedDecoder
  implicit val DeleteCountDecoder:  Decoder[DeleteCount]      = deriveUnwrappedDecoder
  implicit val ExistsDecoder:       Decoder[Exists]           = deriveUnwrappedDecoder

}
