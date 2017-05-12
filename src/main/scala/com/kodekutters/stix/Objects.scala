package com.kodekutters.stix

import java.time.{ZoneId, ZonedDateTime}
import java.util.UUID

import io.circe.syntax._
import io.circe.{Json, _}
import io.circe.generic.auto._
import io.circe.Decoder._

/**
  * STIX-2.1 protocol
  *
  * https://docs.google.com/document/d/1nipwFIaFwkHo4Gzw-qxZQpCjP_5tX7rbI3Ic5C56Z88/edit
  *
  * Author: R. Wathelet May 2017
  */

//-----------------------------------------------------------------------
//------------------supporting data types--------------------------------
//-----------------------------------------------------------------------

/**
  * a valid RFC 3339-formatted timestamp [RFC3339] using the format YYYY-MM-DDTHH:mm:ss[.s+]Z
  * where the “s+” represents 1 or more sub-second values.
  *
  * @param time the time formatted as YYYY-MM-DDTHH:mm:ss[.s+]Z
  */
case class Timestamp(time: String)

object Timestamp {

  implicit val encodeTimestamp: Encoder[Timestamp] = (a: Timestamp) => a.time.asJson

  implicit val decodeTimestamp: Decoder[Timestamp] = (c: HCursor) => for {s <- c.value.as[String]} yield new Timestamp(s)

  def now() = new Timestamp(ZonedDateTime.now(ZoneId.of("Z")).toString)
}

/**
  * An identifier universally and uniquely identifies a SDO, SRO, Bundle, or Marking Definition.
  *
  * @param `type` the type property of the object being identified or referenced
  * @param id     an RFC 4122-compliant Version 4 UUID as a string
  */
case class Identifier(`type`: String, id: String) {
  override def toString = `type` + "--" + id
}

object Identifier {

  implicit val encodeIdentifier: Encoder[Identifier] = (iden: Identifier) => iden.toString.asJson

  implicit val decodeIdentifier: Decoder[Identifier] = (c: HCursor) => for {s <- c.value.as[String]} yield stringToIdentifier(s)

  def stringToIdentifier(s: String): Identifier = {
    val part = s.split("--")
    new Identifier(part(0), part(1))
  }

  def apply(objType: String, uuidv4: String) = new Identifier(objType, uuidv4)

  def apply(objType: String) = new Identifier(objType, UUID.randomUUID().toString)
}

/**
  * The kill-chain-phase represents a phase in a kill chain, which describes the various phases
  * an attacker may undertake in order to achieve their objectives.
  */
case class KillChainPhase(kill_chain_name: String, phase_name: String) {
  val `type` = KillChainPhase.`type`
}

object KillChainPhase {
  val `type` = "kill-chain-phase"
}

//-----------------------------------------------------------------------
//------------------Marking----------------------------------------------
//-----------------------------------------------------------------------

sealed trait MarkingObject

/**
  * TLP levels
  */
sealed case class TLPlevels(value: String)

object TLPlevels {

  object red extends TLPlevels("red")

  object amber extends TLPlevels("amber")

  object green extends TLPlevels("green")

  object white extends TLPlevels("white")

  val values = Seq(red, amber, green, white)

  def fromString(s: String): TLPlevels = {
    s match {
      case red.value => new TLPlevels(red.value.toString)
      case amber.value => new TLPlevels(amber.value.toString)
      case green.value => new TLPlevels(green.value.toString)
      case white.value => new TLPlevels(white.value.toString)
      case _ => new TLPlevels(white.value.toString) // todo <-----what is the default here
    }
  }

  implicit val encodeTLPlevels: Encoder[TLPlevels] = (v: TLPlevels) => v.value.toString.asJson

  implicit val decodeTLPlevels: Decoder[TLPlevels] = (c: HCursor) => for {s <- c.value.as[String]} yield TLPlevels.fromString(s)

}

/**
  * The TLP marking type defines how you would represent a Traffic Light Protocol (TLP) marking in a definition property.
  *
  * @param tlp the tlp level MUST be one of white, green, red, amber
  */
case class TPLMarking(tlp: TLPlevels) extends MarkingObject

/**
  * The Statement marking type defines the representation of a textual marking statement
  * (e.g., copyright, terms of use, etc.) in a definition.
  *
  * @param statement the statement string
  */
case class StatementMarking(statement: String) extends MarkingObject

/**
  * Marking Object, TPLMarking or StatementMarking
  */
object MarkingObject {

  import TLPlevels.encodeTLPlevels
  import TLPlevels.decodeTLPlevels

  implicit val decodeMarkingObject: Decoder[MarkingObject] =
    Decoder[TPLMarking].map(p => p).or(Decoder[StatementMarking].map(p => p))

  implicit val encodeMarkingObject: Encoder[MarkingObject] = new Encoder[MarkingObject] {
    final def apply(mObj: MarkingObject): Json = {
      val jsVal: Json = mObj match {
        case s: TPLMarking => mObj.asInstanceOf[TPLMarking].asJson
        case s: StatementMarking => mObj.asInstanceOf[StatementMarking].asJson
        case _ => Json.obj() // an empty json
      }
      jsVal
    }
  }

}

/**
  * granular markings allow data markings to be applied to individual portions of STIX Objects
  * and Marking Definitions.
  */
case class GranularMarking(selectors: List[String], marking_ref: Option[String] = None, lang: Option[String] = None)

/**
  * External references are used to describe pointers to information represented outside of STIX.
  */
case class ExternalReference(source_name: String, description: Option[String] = None, url: Option[String] = None, external_id: Option[String] = None)

/**
  * The marking-definition object represents a specific marking.
  */
case class MarkingDefinition(id: Identifier,
                             created: Timestamp,
                             definition_type: String,
                             definition: MarkingObject,
                             external_references: Option[List[ExternalReference]] = None,
                             object_marking_refs: Option[List[Identifier]] = None,
                             granular_markings: Option[List[GranularMarking]] = None,
                             created_by_ref: Option[Identifier] = None) {

  val `type` = MarkingDefinition.`type`
}

object MarkingDefinition {
  val `type` = "marking-definition"
}

//-----------------------------------------------------------------------
//------------------STIX Domain Objects----------------------------------
//-----------------------------------------------------------------------

// todo default values for all SDO, e.g. id, created, modified, etc...

/**
  * common properties of all SDO and SRO
  */
sealed trait SDO {
  val `type`: String
  val id: Identifier
  val created: Timestamp
  val modified: Timestamp
  val created_by_ref: Option[Identifier]
  val revoked: Option[Boolean]
  val labels: Option[List[String]]
  val confidence: Option[Int]
  val external_references: Option[List[ExternalReference]]
  val lang: Option[String]
  val object_marking_refs: Option[List[Identifier]]
  val granular_markings: Option[List[GranularMarking]]
}

/**
  * Attack Patterns are a type of TTP that describe ways that adversaries attempt to compromise targets.
  */
case class AttackPattern(name: String, id: Identifier, created: Timestamp, modified: Timestamp,
                         description: Option[String] = None,
                         kill_chain_phases: Option[KillChainPhase] = None,
                         revoked: Option[Boolean] = None,
                         labels: Option[List[String]] = None,
                         confidence: Option[Int] = None,
                         external_references: Option[List[ExternalReference]] = None,
                         lang: Option[String] = None,
                         object_marking_refs: Option[List[Identifier]] = None,
                         granular_markings: Option[List[GranularMarking]] = None,
                         created_by_ref: Option[Identifier] = None) extends SDO {

  val `type` = AttackPattern.`type`
}

object AttackPattern {
  val `type` = "attack-pattern"
}

/**
  * Identities can represent actual individuals, organizations, or groups (e.g., ACME, Inc.) as well as
  * classes of individuals, organizations, or groups (e.g., the finance sector).
  */
case class Identity(name: String, identity_class: String, id: Identifier, created: Timestamp, modified: Timestamp,
                    sectors: Option[List[String]] = None,
                    contact_information: Option[String] = None,
                    description: Option[String] = None,
                    revoked: Option[Boolean] = None,
                    labels: Option[List[String]] = None,
                    confidence: Option[Int] = None,
                    external_references: Option[List[ExternalReference]] = None,
                    lang: Option[String] = None,
                    object_marking_refs: Option[List[Identifier]] = None,
                    granular_markings: Option[List[GranularMarking]] = None,
                    created_by_ref: Option[Identifier] = None) extends SDO {

  val `type` = Identity.`type`
}

object Identity {
  val `type` = "identity"
}

/**
  * A Campaign is a grouping of adversarial behaviors that describes a set of malicious activities or
  * attacks (sometimes called waves) that occur over a period of time against a specific set of targets.
  * Campaigns usually have well defined objectives and may be part of an Intrusion Set.
  */
case class Campaign(name: String, id: Identifier, created: Timestamp, modified: Timestamp,
                    description: Option[String] = None, aliases: Option[List[String]] = None,
                    first_seen: Option[Timestamp] = None, last_seen: Option[Timestamp] = None, objective: Option[String] = None,
                    revoked: Option[Boolean] = None,
                    labels: Option[List[String]] = None,
                    confidence: Option[Int] = None,
                    external_references: Option[List[ExternalReference]] = None,
                    lang: Option[String] = None,
                    object_marking_refs: Option[List[Identifier]] = None,
                    granular_markings: Option[List[GranularMarking]] = None,
                    created_by_ref: Option[Identifier] = None) extends SDO {

  val `type` = Campaign.`type`
}

object Campaign {
  val `type` = "campaign"
}

/**
  * A Course of Action is an action taken either to prevent an attack or to respond to an attack that is in progress.
  */
case class CourseOfAction(name: String, id: Identifier, created: Timestamp, modified: Timestamp,
                          description: Option[String] = None,
                          revoked: Option[Boolean] = None,
                          labels: Option[List[String]] = None,
                          confidence: Option[Int] = None,
                          external_references: Option[List[ExternalReference]] = None,
                          lang: Option[String] = None,
                          object_marking_refs: Option[List[Identifier]] = None,
                          granular_markings: Option[List[GranularMarking]] = None,
                          created_by_ref: Option[Identifier] = None) extends SDO {

  val `type` = CourseOfAction.`type`
}

object CourseOfAction {
  val `type` = "course-of-action"
}

/**
  * Indicators contain a pattern that can be used to detect suspicious or malicious cyber activity.
  */
case class Indicator(pattern: String, valid_from: Timestamp, valid_until: Timestamp,
                     id: Identifier, created: Timestamp, modified: Timestamp,
                     kill_chain_phases: Option[KillChainPhase] = None,
                     labels: Option[List[String]], // todo ---> should not be optional
                     name: Option[String] = None,
                     description: Option[String] = None,
                     revoked: Option[Boolean] = None,
                     confidence: Option[Int] = None,
                     external_references: Option[List[ExternalReference]] = None,
                     lang: Option[String] = None,
                     object_marking_refs: Option[List[Identifier]] = None,
                     granular_markings: Option[List[GranularMarking]] = None,
                     created_by_ref: Option[Identifier] = None) extends SDO {

  val `type` = Indicator.`type`
}

object Indicator {
  val `type` = "indicator"
}

/**
  * An Intrusion Set is a grouped set of adversarial behaviors and resources with common properties that is believed
  * to be orchestrated by a single organization.
  */
case class IntrusionSet(name: String, id: Identifier, created: Timestamp, modified: Timestamp,
                        description: Option[String] = None,
                        first_seen: Option[Timestamp] = None, last_seen: Option[Timestamp] = None, goals: Option[List[String]] = None,
                        resource_level: Option[String] = None,
                        primary_motivation: Option[String] = None,
                        revoked: Option[Boolean] = None,
                        labels: Option[List[String]] = None,
                        confidence: Option[Int] = None,
                        external_references: Option[List[ExternalReference]] = None,
                        lang: Option[String] = None,
                        object_marking_refs: Option[List[Identifier]] = None,
                        granular_markings: Option[List[GranularMarking]] = None,
                        created_by_ref: Option[Identifier] = None) extends SDO {

  val `type` = IntrusionSet.`type`
}

object IntrusionSet {
  val `type` = "intrusion-set"
}

/**
  * Malware is a type of TTP that is also known as malicious code and malicious software,
  * and refers to a program that is inserted into a system, usually covertly,
  * with the intent of compromising the confidentiality, integrity, or availability of
  * the victim's data, applications, or operating system (OS) or of otherwise annoying or
  * disrupting the victim.
  */
case class Malware(name: String, id: Identifier, created: Timestamp, modified: Timestamp,
                   description: Option[String] = None,
                   kill_chain_phases: Option[KillChainPhase] = None,
                   revoked: Option[Boolean] = None,
                   labels: Option[List[String]] = None,
                   confidence: Option[Int] = None,
                   external_references: Option[List[ExternalReference]] = None,
                   lang: Option[String] = None,
                   object_marking_refs: Option[List[Identifier]] = None,
                   granular_markings: Option[List[GranularMarking]] = None,
                   created_by_ref: Option[Identifier] = None) extends SDO {

  val `type` = Malware.`type`
}

object Malware {
  val `type` = "malware"
}

// todo ObservableObjects

/**
  * Observed Data conveys information that was observed on systems and networks using the Cyber Observable specification
  * defined in parts 3 and 4 of this specification.
  */
case class ObservedData(name: String, id: Identifier, created: Timestamp, modified: Timestamp,
                        first_observed: Timestamp, last_observed: Timestamp, number_observed: Int,
                        objects: Map[String, String], // ObservableObjects
                        description: Option[String] = None,
                        revoked: Option[Boolean] = None,
                        labels: Option[List[String]] = None,
                        confidence: Option[Int] = None,
                        external_references: Option[List[ExternalReference]] = None,
                        lang: Option[String] = None,
                        object_marking_refs: Option[List[Identifier]] = None,
                        granular_markings: Option[List[GranularMarking]] = None,
                        created_by_ref: Option[Identifier] = None) extends SDO {

  val `type` = ObservedData.`type`
}

object ObservedData {
  val `type` = "observed-data"
}

/**
  * Reports are collections of threat intelligence focused on one or more topics, such as a description of
  * a threat actor, malware, or attack technique, including context and related details.
  */
case class Report(name: String, published: Timestamp, id: Identifier, created: Timestamp, modified: Timestamp,
                  object_refs: Option[List[Identifier]] = None,
                  description: Option[String] = None,
                  revoked: Option[Boolean] = None,
                  labels: Option[List[String]] = None,
                  confidence: Option[Int] = None,
                  external_references: Option[List[ExternalReference]] = None,
                  lang: Option[String] = None,
                  object_marking_refs: Option[List[Identifier]] = None,
                  granular_markings: Option[List[GranularMarking]] = None,
                  created_by_ref: Option[Identifier] = None) extends SDO {

  val `type` = Report.`type`
}

object Report {
  val `type` = "report"
}

/**
  * Threat Actors are actual individuals, groups,
  * or organizations believed to be operating with malicious intent.
  */
case class ThreatActor(name: String, id: Identifier, created: Timestamp, modified: Timestamp,
                       labels: Option[List[String]], // todo ---> should not be optional
                       description: Option[String] = None,
                       aliases: Option[String] = None,
                       roles: Option[List[String]] = None,
                       goals: Option[List[String]] = None,
                       resource_level: Option[String] = None,
                       primary_motivation: Option[String] = None,
                       secondary_motivations: Option[List[String]] = None,
                       personal_motivations: Option[List[String]] = None,
                       revoked: Option[Boolean] = None,
                       confidence: Option[Int] = None,
                       external_references: Option[List[ExternalReference]] = None,
                       lang: Option[String] = None,
                       object_marking_refs: Option[List[Identifier]] = None,
                       granular_markings: Option[List[GranularMarking]] = None,
                       created_by_ref: Option[Identifier] = None) extends SDO {

  val `type` = ThreatActor.`type`
}

object ThreatActor {
  val `type` = "threat-actor"
}

/**
  * Tools are legitimate software that can be used by threat actors to perform attacks.
  */
case class Tool(name: String, id: Identifier, created: Timestamp, modified: Timestamp,
                labels: Option[List[String]], // todo ---> should not be optional
                description: Option[String] = None,
                kill_chain_phases: Option[List[KillChainPhase]] = None,
                tool_version: Option[String] = None,
                revoked: Option[Boolean] = None,
                confidence: Option[Int] = None,
                external_references: Option[List[ExternalReference]] = None,
                lang: Option[String] = None,
                object_marking_refs: Option[List[Identifier]] = None,
                granular_markings: Option[List[GranularMarking]] = None,
                created_by_ref: Option[Identifier] = None) extends SDO {

  val `type` = Tool.`type`
}

object Tool {
  val `type` = "tool"
}

/**
  * A Vulnerability is "a mistake in software that can be directly used by a hacker
  * to gain access to a system or network"
  */
case class Vulnerability(name: String, id: Identifier, created: Timestamp, modified: Timestamp,
                         description: Option[String] = None,
                         revoked: Option[Boolean] = None,
                         labels: Option[List[String]] = None,
                         confidence: Option[Int] = None,
                         external_references: Option[List[ExternalReference]] = None,
                         lang: Option[String] = None,
                         object_marking_refs: Option[List[Identifier]] = None,
                         granular_markings: Option[List[GranularMarking]] = None,
                         created_by_ref: Option[Identifier] = None) extends SDO {

  val `type` = Vulnerability.`type`
}

object Vulnerability {
  val `type` = "vulnerability"
}

//-----------------------------------------------------------------------
//------------------Relationship objects----------------------------------
//-----------------------------------------------------------------------

sealed trait SRO

/**
  * The Relationship object is used to link together two SDOs in order to describe how
  * they are related to each other. If SDOs are considered "nodes" or "vertices" in the graph,
  * the Relationship Objects (SROs) represent "edges".
  */
case class Relationship(source_ref: Identifier,
                        relationship_type: String,
                        target_ref: Identifier,
                        id: Identifier,
                        created: Timestamp, modified: Timestamp,
                        description: Option[String] = None,
                        revoked: Option[Boolean] = None,
                        labels: Option[List[String]] = None,
                        confidence: Option[Int] = None,
                        external_references: Option[List[ExternalReference]] = None,
                        lang: Option[String] = None,
                        object_marking_refs: Option[List[Identifier]] = None,
                        granular_markings: Option[List[GranularMarking]] = None,
                        created_by_ref: Option[Identifier] = None) extends SDO with SRO {

  val `type` = Relationship.`type`

  def this(source_ref: Identifier, relationship_type: String, target_ref: Identifier) =
    this(source_ref, relationship_type, target_ref,
      Identifier(Relationship.`type`, UUID.randomUUID().toString), Timestamp.now(), Timestamp.now())
}

object Relationship {
  val `type` = "relationship"
}

/**
  * A Sighting denotes the belief that something in CTI (e.g., an indicator, malware, tool, threat actor, etc.) was seen.
  */
case class Sighting(relationship_type: String, sighting_of_ref: Identifier, id: Identifier,
                    created: Timestamp, modified: Timestamp,
                    first_seen: Option[Timestamp] = None, last_seen: Option[Timestamp] = None,
                    count: Option[Int] = None,
                    observed_data_refs: Option[List[Identifier]] = None,
                    where_sighted_refs: Option[List[Identifier]] = None,
                    summary: Option[Boolean] = None,
                    description: Option[String] = None,
                    revoked: Option[Boolean] = None,
                    labels: Option[List[String]] = None,
                    confidence: Option[Int] = None,
                    external_references: Option[List[ExternalReference]] = None,
                    lang: Option[String] = None,
                    object_marking_refs: Option[List[Identifier]] = None,
                    granular_markings: Option[List[GranularMarking]] = None,
                    created_by_ref: Option[Identifier] = None) extends SDO with SRO {

  val `type` = Sighting.`type`
}

object Sighting {
  val `type` = "sighting"
}

//-----------------------------------------------------------------------
//------------------SDO and Bundle object----------------------------------------
//-----------------------------------------------------------------------

object SDO {

  import Timestamp.decodeTimestamp
  import Timestamp.encodeTimestamp
  import Identifier.decodeIdentifier
  import Identifier.encodeIdentifier

  implicit val decodeSDO: Decoder[SDO] = Decoder.instance(c =>
    c.downField("type").as[String].right.flatMap {
      case AttackPattern.`type` => c.as[AttackPattern]
      case Identity.`type` => c.as[Identity]
      case Campaign.`type` => c.as[Campaign]
      case CourseOfAction.`type` => c.as[CourseOfAction]
      case Indicator.`type` => c.as[Indicator]
      case IntrusionSet.`type` => c.as[IntrusionSet]
      case Malware.`type` => c.as[Malware]
      case ObservedData.`type` => c.as[ObservedData]
      case Report.`type` => c.as[Report]
      case ThreatActor.`type` => c.as[ThreatActor]
      case Tool.`type` => c.as[Tool]
      case Vulnerability.`type` => c.as[Vulnerability]
      case Relationship.`type` => c.as[Relationship]
      case Sighting.`type` => c.as[Sighting]
      //  case err => c.as[SDOError]
    })

  implicit val encodeSDO: Encoder[SDO] = new Encoder[SDO] {
    final def apply(sdo: SDO): Json = {
      val jsVal: Json = sdo match {
        case s: AttackPattern => sdo.asInstanceOf[AttackPattern].asJson
        case s: Identity => sdo.asInstanceOf[Identity].asJson
        case s: Campaign => sdo.asInstanceOf[Campaign].asJson
        case s: CourseOfAction => sdo.asInstanceOf[CourseOfAction].asJson
        case s: Indicator => sdo.asInstanceOf[Indicator].asJson
        case s: IntrusionSet => sdo.asInstanceOf[IntrusionSet].asJson
        case s: Malware => sdo.asInstanceOf[Malware].asJson
        case s: ObservedData => sdo.asInstanceOf[ObservedData].asJson
        case s: Report => sdo.asInstanceOf[Report].asJson
        case s: ThreatActor => sdo.asInstanceOf[ThreatActor].asJson
        case s: Tool => sdo.asInstanceOf[Tool].asJson
        case s: Vulnerability => sdo.asInstanceOf[Vulnerability].asJson
        case s: Relationship => sdo.asInstanceOf[Relationship].asJson
        case s: Sighting => sdo.asInstanceOf[Sighting].asJson
        case _ => Json.obj() // an empty json
      }
      jsVal //.deepMerge(Json.obj("type" -> Json.fromString(sdo.`type`)))
    }
  }

}

/**
  * A Bundle is a collection of arbitrary STIX Objects and Marking Definitions grouped together in a single container.
  *
  * @param id      An identifier for this Bundle.
  * @param objects Specifies a set of one or more STIX Objects.
  */
case class Bundle(id: Identifier, objects: List[SDO]) {
  val `type` = Bundle.`type`
  val spec_version = Bundle.spec_version
}

object Bundle {
  val `type` = "bundle"
  val spec_version = "2.1"
}

