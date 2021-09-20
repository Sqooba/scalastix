package com.kodekutters.stix

import io.circe.generic.JsonCodec
import io.circe.{Encoder, Json}


/**
  * A Bundle is a collection of arbitrary STIX Objects and Marking Definitions grouped together in a single container.
  *
  * @param id      An identifier for this Bundle.
  * @param objects Specifies a set of one or more STIX Objects.
  */
@JsonCodec case class Bundle(`type`: String = Bundle.`type`,
                             id: Identifier = Identifier(Bundle.`type`),
                             objects: Seq[Json] = Seq()) {

  def add[T <: STIX](obj: T)(implicit encoder: Encoder[T]): Bundle =
    copy(objects = objects :+ encoder(obj))

}

object Bundle {
  val `type` = "bundle"
}
