package com.kodekutters.stix

import io.circe.{Decoder, DecodingFailure, Encoder, HCursor, Json}

import java.util.UUID

/**
  * An identifier universally and uniquely identifies a SDO, SRO, Bundle, or Marking Definition.
  *
  * @param objType the type property of the object being identified or referenced
  * @param id      an RFC 4122-compliant Version 4 UUID as a string
  */
case class Identifier(objType: String, id: String) {
  val `type` = Identifier.`type`

  override def toString = objType + "--" + id
}

object Identifier {
  val `type` = "identifier"

  def apply(objType: String) = new Identifier(objType, UUID.randomUUID().toString)

  implicit val identifierEncoder: Encoder[Identifier] = (a: Identifier) => Json.fromString(s"${a.objType}--${a.id}")
  implicit val identifierDecoder: Decoder[Identifier] = (c: HCursor) =>
    c
      .as[String]
      .flatMap(s => Option(s.split("--")).filter(_.length == 2).toRight(DecodingFailure("invalid STIX ID", c.history)))
      .map(flds => Identifier(flds(0), flds(1)))
}
