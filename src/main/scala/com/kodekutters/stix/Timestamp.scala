package com.kodekutters.stix

import io.circe.{Decoder, Encoder, HCursor, Json}

import java.time.{ZoneId, ZonedDateTime}

/**
  * a valid RFC 3339-formatted timestamp [RFC3339] using the format YYYY-MM-DDTHH:mm:ss[.s+]Z
  * where the “s+” represents 1 or more sub-second values.
  *
  * @param time the time formatted as YYYY-MM-DDTHH:mm:ss[.s+]Z
  */
case class Timestamp(time: String) {
  val `type` = Timestamp.`type`

  override def toString: String = time
}

object Timestamp {
  val `type` = "timestamp"

  def now() = new Timestamp(ZonedDateTime.now(ZoneId.of("Z")).toString)

  implicit val timestampEncoder: Encoder[Timestamp] = (a: Timestamp) => Json.fromString(a.time)
  implicit val timestampDecoder: Decoder[Timestamp] = (c: HCursor) => c.as[String].map(Timestamp(_))
}
