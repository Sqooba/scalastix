package com.kodekutters.stix

import io.circe.{Decoder, Encoder, HCursor, Json}

object CustomCodecs {
  implicit val eitherLongStringEncoder: Encoder[Either[Long, String]] = (a: Either[Long, String]) => a.fold(Json.fromLong, Json.fromString)
  implicit val eitherLongStringDecoder: Decoder[Either[Long, String]] = (c: HCursor) => {
    // try long extraction
    val long = c.as[Long].map(Left[Long, String])
    // fallback to string extraction
    long.left.flatMap(_ => c.as[String].map(Right[Long, String]))
  }
}
