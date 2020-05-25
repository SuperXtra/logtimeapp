package data


import org.joda.time.DateTime
//
//// TODO this might be solution for problem with dates
object JodaDateTimeString  {

  implicit val JodaDateTimeDecoder: io.circe.Decoder[DateTime] =
    io.circe.Decoder.decodeString.map(DateTime.parse).or(io.circe.Decoder.decodeLong.map(v => new DateTime(v)))
  implicit val JodaDateTimeEncoder: io.circe.Encoder[DateTime] =
    io.circe.Encoder.encodeString.contramap[DateTime](_.toString)
}
