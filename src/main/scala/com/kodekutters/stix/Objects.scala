package com.kodekutters.stix

import io.circe.Json
import io.circe.generic.JsonCodec

/**
  * STIX-2.0 protocol
  *
  * reference: https://oasis-open.github.io/cti-documentation/
  *
  * Author: Ringo Wathelet 2017
  */


//-----------------------------------------------------------------------
//------------------Marking----------------------------------------------
//-----------------------------------------------------------------------

@JsonCodec sealed trait MarkingObject

/**
  * TLP levels
  */
@JsonCodec sealed case class TLPlevels(value: String)

object TLPlevels {

  object red extends TLPlevels("red")

  object amber extends TLPlevels("amber")

  object green extends TLPlevels("green")

  object white extends TLPlevels("white")

  val values = Seq(red, amber, green, white)

  def fromString(s: String): TLPlevels = {
    s match {
      case red.value => new TLPlevels(red.value)
      case amber.value => new TLPlevels(amber.value)
      case green.value => new TLPlevels(green.value)
      case white.value => new TLPlevels(white.value)
      case x => new TLPlevels(x) // todo is this correct <------
    }
  }

}

// As defined in: https://docs.oasis-open.org/cti/stix/v2.1/cs02/stix-v2.1-cs02.html#_yd3ar14ekwrs
object TLP {
  val WHITE: Identifier = Identifier("marking-definition", "613f2e26-407d-48c7-9eca-b8e91df99dc9")
  val GREEN: Identifier = Identifier("marking-definition", "34098fce-860f-48ae-8e50-ebd3cc5e41da")
  val AMBER: Identifier = Identifier("marking-definition", "f88d31f6-486f-44da-b317-01333bde0b82")
  val RED: Identifier = Identifier("marking-definition", "5e57c739-391a-4eb3-b6be-7d15ca92d5ed")
}

/**
  * The TLP marking type defines how you would represent a Traffic Light Protocol (TLP) marking in a definition property.
  *
  * @param tlp the tlp level MUST be one of white, green, red, amber
  */
@JsonCodec case class TLPMarking(tlp: TLPlevels) extends MarkingObject

/**
  * The Statement marking type defines the representation of a textual marking statement
  * (e.g., copyright, terms of use, etc.) in a definition.
  *
  * @param statement the statement string
  */
@JsonCodec case class StatementMarking(statement: String) extends MarkingObject

/**
  * granular markings allow data markings to be applied to individual portions of STIX Objects
  * and Marking Definitions.
  */
@JsonCodec case class GranularMarking(selectors: List[String], marking_ref: Option[String] = None, lang: Option[String] = None) {
  val `type` = GranularMarking.`type`
}

object GranularMarking {
  val `type` = "granular-marking"
}

/**
  * External references are used to describe pointers to information represented outside of STIX.
  */
@JsonCodec case class ExternalReference(source_name: String, description: Option[String] = None,
                             external_id: Option[String] = None,
                             url: Option[String] = None,
                             hashes: Option[Map[String, String]] = None) {
  val `type` = ExternalReference.`type`
}

object ExternalReference {
  val `type` = "external-reference"
}

//-----------------------------------------------------------------------
//------------------x-custom properties support--------------------------
//-----------------------------------------------------------------------

/**
  * a generic custom (key,value) dictionary representing the Custom Properties,
  * with key = a custom property name, i.e. starting with "x_", and value = the property JsValue
  */
@JsonCodec case class CustomProps(nodes: Map[String, Json])

//-----------------------------------------------------------------------

/**
  * a general STIX object representing the SDOs, SROs and MarkingDefinition (and LanguageContent)
  * all StixObj can be added to a Bundle.
  */
sealed trait StixObj extends STIX {
  def custom: Option[CustomProps] // the custom properties as a map of property names and JsValues
}

/**
  * The marking-definition object represents a specific marking.
  */
@JsonCodec case class MarkingDefinition(`type`: String = MarkingDefinition.`type`,
                                        id: Identifier = Identifier(MarkingDefinition.`type`),
                                        created: Timestamp = Timestamp.now(),
                                        definition_type: String,
                                        definition: MarkingObject,
                                        external_references: Option[List[ExternalReference]] = None,
                                        object_marking_refs: Option[List[Identifier]] = None,
                                        granular_markings: Option[List[GranularMarking]] = None,
                                        created_by_ref: Option[Identifier] = None,
                                        custom: Option[CustomProps] = None,
                                        spec_version: String = SPEC_VERSION) extends StixObj

object MarkingDefinition {
  val `type` = "marking-definition"
}

trait CoreObject extends StixObj {
  val created: Timestamp
  val modified: Timestamp
  val created_by_ref: Option[Identifier]
  val revoked: Option[Boolean]
  val labels: Option[List[String]]
  val external_references: Option[List[ExternalReference]]
  val object_marking_refs: Option[List[Identifier]]
  val granular_markings: Option[List[GranularMarking]]
}

//-----------------------------------------------------------------------
//------------------STIX Domain Objects----------------------------------
//-----------------------------------------------------------------------

/**
  * common properties of all SDO
  */
@JsonCodec sealed trait SDO extends CoreObject {}

/**
  * Attack Patterns are a type of TTP that describe ways that adversaries attempt to compromise targets.
  */
@JsonCodec case class AttackPattern(`type`: String = AttackPattern.`type`,
                                    id: Identifier = Identifier(AttackPattern.`type`),
                                    created: Timestamp = Timestamp.now(),
                                    modified: Timestamp = Timestamp.now(),
                                    name: String,
                                    description: Option[String] = None,
                                    kill_chain_phases: Option[List[KillChainPhase]] = None,
                                    revoked: Option[Boolean] = None,
                                    labels: Option[List[String]] = None,
                                    external_references: Option[List[ExternalReference]] = None,
                                    object_marking_refs: Option[List[Identifier]] = None,
                                    granular_markings: Option[List[GranularMarking]] = None,
                                    created_by_ref: Option[Identifier] = None,
                                    custom: Option[CustomProps] = None,
                                    spec_version: String = SPEC_VERSION) extends SDO

object AttackPattern {
  val `type` = "attack-pattern"
}

/**
  * Identities can represent actual individuals, organizations, or groups (e.g., ACME, Inc.) as well as
  * classes of individuals, organizations, or groups (e.g., the finance sector).
  */
@JsonCodec case class Identity(`type`: String = Identity.`type`,
                               id: Identifier = Identifier(Identity.`type`),
                               created: Timestamp = Timestamp.now(),
                               modified: Timestamp = Timestamp.now(),
                               name: String,
                               identity_class: String,
                               sectors: Option[List[String]] = None,
                               contact_information: Option[String] = None,
                               description: Option[String] = None,
                               revoked: Option[Boolean] = None,
                               labels: Option[List[String]] = None,
                               external_references: Option[List[ExternalReference]] = None,
                               object_marking_refs: Option[List[Identifier]] = None,
                               granular_markings: Option[List[GranularMarking]] = None,
                               created_by_ref: Option[Identifier] = None,
                               custom: Option[CustomProps] = None,
                               spec_version: String = SPEC_VERSION) extends SDO

object Identity {
  val `type` = "identity"
}

/**
  * A Campaign is a grouping of adversarial behaviors that describes a set of malicious activities or
  * attacks (sometimes called waves) that occur over a period of time against a specific set of targets.
  * Campaigns usually have well defined objectives and may be part of an Intrusion Set.
  */
@JsonCodec case class Campaign(`type`: String = Campaign.`type`,
                               id: Identifier = Identifier(Campaign.`type`),
                               created: Timestamp = Timestamp.now(),
                               modified: Timestamp = Timestamp.now(),
                               name: String,
                               description: Option[String] = None,
                               aliases: Option[List[String]] = None,
                               first_seen: Option[Timestamp] = None,
                               last_seen: Option[Timestamp] = None,
                               objective: Option[String] = None,
                               revoked: Option[Boolean] = None,
                               labels: Option[List[String]] = None,
                               external_references: Option[List[ExternalReference]] = None,
                               object_marking_refs: Option[List[Identifier]] = None,
                               granular_markings: Option[List[GranularMarking]] = None,
                               created_by_ref: Option[Identifier] = None,
                               custom: Option[CustomProps] = None,
                               spec_version: String = SPEC_VERSION) extends SDO

object Campaign {
  val `type` = "campaign"
}

/**
  * A Course of Action is an action taken either to prevent an attack or to respond to an attack that is in progress.
  */
@JsonCodec case class CourseOfAction(`type`: String = CourseOfAction.`type`,
                                     id: Identifier = Identifier(CourseOfAction.`type`),
                                     created: Timestamp = Timestamp.now(),
                                     modified: Timestamp = Timestamp.now(),
                                     name: String,
                                     description: Option[String] = None,
                                     revoked: Option[Boolean] = None,
                                     labels: Option[List[String]] = None,
                                     external_references: Option[List[ExternalReference]] = None,
                                     object_marking_refs: Option[List[Identifier]] = None,
                                     granular_markings: Option[List[GranularMarking]] = None,
                                     created_by_ref: Option[Identifier] = None,
                                     custom: Option[CustomProps] = None,
                                     spec_version: String = SPEC_VERSION) extends SDO

object CourseOfAction {
  val `type` = "course-of-action"
}

/**
  * Indicators contain a pattern that can be used to detect suspicious or malicious cyber activity.
  */
@JsonCodec case class Indicator(`type`: String = Indicator.`type`,
                                id: Identifier = Identifier(Indicator.`type`),
                                created: Timestamp = Timestamp.now(),
                                modified: Timestamp = Timestamp.now(),
                                pattern: String,
                                valid_from: Timestamp,
                                name: Option[String] = None,
                                valid_until: Option[Timestamp] = None,
                                labels: Option[List[String]] = None, // todo ---> should not be optional
                                kill_chain_phases: Option[List[KillChainPhase]] = None,
                                description: Option[String] = None,
                                revoked: Option[Boolean] = None,
                                external_references: Option[List[ExternalReference]] = None,
                                object_marking_refs: Option[List[Identifier]] = None,
                                granular_markings: Option[List[GranularMarking]] = None,
                                created_by_ref: Option[Identifier] = None,
                                custom: Option[CustomProps] = None,
                                spec_version: String = SPEC_VERSION) extends SDO

object Indicator {
  val `type` = "indicator"
}

/**
  * An Intrusion Set is a grouped set of adversarial behaviors and resources with common properties that is believed
  * to be orchestrated by a single organization.
  *
  */
@JsonCodec case class IntrusionSet(`type`: String = IntrusionSet.`type`,
                        id: Identifier = Identifier(IntrusionSet.`type`),
                        created: Timestamp = Timestamp.now(),
                        modified: Timestamp = Timestamp.now(),
                        name: String,
                        description: Option[String] = None,
                        aliases: Option[List[String]] = None,
                        first_seen: Option[Timestamp] = None,
                        last_seen: Option[Timestamp] = None,
                        goals: Option[List[String]] = None,
                        resource_level: Option[String] = None,
                        primary_motivation: Option[String] = None,
                        secondary_motivations: Option[List[String]] = None,
                        revoked: Option[Boolean] = None,
                        labels: Option[List[String]] = None, // todo ---> should not be optional
                        external_references: Option[List[ExternalReference]] = None,
                        object_marking_refs: Option[List[Identifier]] = None,
                        granular_markings: Option[List[GranularMarking]] = None,
                        created_by_ref: Option[Identifier] = None,
                        custom: Option[CustomProps] = None,
                                   spec_version: String = SPEC_VERSION) extends SDO

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
@JsonCodec case class Malware(`type`: String = Malware.`type`,
                              id: Identifier = Identifier(Malware.`type`),
                              created: Timestamp = Timestamp.now(),
                              modified: Timestamp = Timestamp.now(),
                              name: String,
                              description: Option[String] = None,
                              kill_chain_phases: Option[List[KillChainPhase]] = None,
                              revoked: Option[Boolean] = None,
                              labels: Option[List[String]] = None,
                              external_references: Option[List[ExternalReference]] = None,
                              object_marking_refs: Option[List[Identifier]] = None,
                              granular_markings: Option[List[GranularMarking]] = None,
                              created_by_ref: Option[Identifier] = None,
                              custom: Option[CustomProps] = None,
                              spec_version: String = SPEC_VERSION) extends SDO

object Malware {
  val `type` = "malware"
}

/**
  * Observed Data conveys information that was observed on systems and networks using the Cyber Observable specification
  * defined in parts 3 and 4 of this specification.
  */
@JsonCodec case class ObservedData(`type`: String = ObservedData.`type`,
                                   id: Identifier = Identifier(ObservedData.`type`),
                                   created: Timestamp = Timestamp.now(),
                                   modified: Timestamp = Timestamp.now(),
                                   first_observed: Timestamp,
                                   last_observed: Timestamp,
                                   number_observed: Long,
                                   objects: Map[String, Observable],
                                   description: Option[String] = None,
                                   revoked: Option[Boolean] = None,
                                   labels: Option[List[String]] = None,
                                   external_references: Option[List[ExternalReference]] = None,
                                   object_marking_refs: Option[List[Identifier]] = None,
                                   granular_markings: Option[List[GranularMarking]] = None,
                                   created_by_ref: Option[Identifier] = None,
                                   custom: Option[CustomProps] = None,
                                   spec_version: String = SPEC_VERSION) extends SDO

object ObservedData {
  val `type` = "observed-data"
}

/**
  * Reports are collections of threat intelligence focused on one or more topics, such as a description of
  * a threat actor, malware, or attack technique, including context and related details.
  */
@JsonCodec case class Report(
  `type`: String = Report.`type`,
  id: Identifier = Identifier(Report.`type`),
  created: Timestamp = Timestamp.now(),
  lang: Option[String] = None,
  modified: Timestamp = Timestamp.now(),
  name: String,
  published: Timestamp,
  object_refs: List[Identifier],
  description: Option[String] = None,
  report_types: Option[List[String]] = None,
  revoked: Option[Boolean] = None,
  labels: Option[List[String]] = None,
  external_references: Option[List[ExternalReference]] = None,
  object_marking_refs: Option[List[Identifier]] = None,
  granular_markings: Option[List[GranularMarking]] = None,
  created_by_ref: Option[Identifier] = None,
  custom: Option[CustomProps] = None,
  spec_version: String = SPEC_VERSION
) extends SDO

object Report {
  val `type` = "report"
}

/**
  * Threat Actors are actual individuals, groups,
  * or organizations believed to be operating with malicious intent.
  */
@JsonCodec case class ThreatActor(`type`: String = ThreatActor.`type`,
                                  id: Identifier = Identifier(ThreatActor.`type`),
                                  created: Timestamp = Timestamp.now(),
                                  modified: Timestamp = Timestamp.now(),
                                  name: String,
                                  labels: Option[List[String]] = None, // todo ---> should not be optional
                                  description: Option[String] = None,
                                  aliases: Option[List[String]] = None,
                                  roles: Option[List[String]] = None,
                                  goals: Option[List[String]] = None,
                                  sophistication: Option[String] = None,
                                  resource_level: Option[String] = None,
                                  primary_motivation: Option[String] = None,
                                  secondary_motivations: Option[List[String]] = None,
                                  personal_motivations: Option[List[String]] = None,
                                  revoked: Option[Boolean] = None,
                                  external_references: Option[List[ExternalReference]] = None,
                                  object_marking_refs: Option[List[Identifier]] = None,
                                  granular_markings: Option[List[GranularMarking]] = None,
                                  created_by_ref: Option[Identifier] = None,
                                  custom: Option[CustomProps] = None,
                                  spec_version: String = SPEC_VERSION) extends SDO

object ThreatActor {
  val `type` = "threat-actor"
}

/**
  * Tools are legitimate software that can be used by threat actors to perform attacks.
  */
@JsonCodec case class Tool(`type`: String = Tool.`type`,
                           id: Identifier = Identifier(Tool.`type`),
                           created: Timestamp = Timestamp.now(),
                           modified: Timestamp = Timestamp.now(),
                           name: String,
                           labels: Option[List[String]] = None, // todo ---> should not be optional
                           description: Option[String] = None,
                           kill_chain_phases: Option[List[KillChainPhase]] = None,
                           tool_version: Option[String] = None,
                           revoked: Option[Boolean] = None,
                           external_references: Option[List[ExternalReference]] = None,
                           object_marking_refs: Option[List[Identifier]] = None,
                           granular_markings: Option[List[GranularMarking]] = None,
                           created_by_ref: Option[Identifier] = None,
                           custom: Option[CustomProps] = None,
                           spec_version: String = SPEC_VERSION) extends SDO

object Tool {
  val `type` = "tool"
}

/**
  * A Vulnerability is "a mistake in software that can be directly used by a hacker
  * to gain access to a system or network"
  */
@JsonCodec case class Vulnerability(`type`: String = Vulnerability.`type`,
                                    id: Identifier = Identifier(Vulnerability.`type`),
                                    created: Timestamp = Timestamp.now(),
                                    modified: Timestamp = Timestamp.now(),
                                    name: String,
                                    description: Option[String] = None,
                                    revoked: Option[Boolean] = None,
                                    labels: Option[List[String]] = None,
                                    external_references: Option[List[ExternalReference]] = None,
                                    object_marking_refs: Option[List[Identifier]] = None,
                                    granular_markings: Option[List[GranularMarking]] = None,
                                    created_by_ref: Option[Identifier] = None,
                                    custom: Option[CustomProps] = None,
                                    spec_version: String = SPEC_VERSION) extends SDO

object Vulnerability {
  val `type` = "vulnerability"
}

/**
  * represents an ad-hock custom stix object
  */
@JsonCodec case class CustomStix(`type`: String = CustomStix.`type`,
                                 id: Identifier = Identifier(CustomStix.`type`),
                                 created: Timestamp = Timestamp.now(),
                                 modified: Timestamp = Timestamp.now(),
                                 revoked: Option[Boolean] = None,
                                 labels: Option[List[String]] = None,
                                 external_references: Option[List[ExternalReference]] = None,
                                 object_marking_refs: Option[List[Identifier]] = None,
                                 granular_markings: Option[List[GranularMarking]] = None,
                                 created_by_ref: Option[Identifier] = None,
                                 custom: Option[CustomProps] = None,
                                 spec_version: String = SPEC_VERSION) extends SDO

object CustomStix {
  val `type` = "x-custom-object"
}

//-----------------------------------------------------------------------
//------------------Relationship objects----------------------------------
//-----------------------------------------------------------------------

@JsonCodec sealed trait SRO extends CoreObject {}

/**
  * The Relationship object is used to link together two SDOs in order to describe how
  * they are related to each other. If SDOs are considered "nodes" or "vertices" in the graph,
  * the Relationship Objects (SROs) represent "edges".
  */
@JsonCodec case class Relationship(`type`: String = Relationship.`type`,
                                   id: Identifier = Identifier(Relationship.`type`),
                                   created: Timestamp = Timestamp.now(),
                                   modified: Timestamp = Timestamp.now(),
                                   source_ref: Identifier,
                                   relationship_type: String,
                                   target_ref: Identifier,
                                   description: Option[String] = None,
                                   revoked: Option[Boolean] = None,
                                   labels: Option[List[String]] = None,
                                   external_references: Option[List[ExternalReference]] = None,
                                   object_marking_refs: Option[List[Identifier]] = None,
                                   granular_markings: Option[List[GranularMarking]] = None,
                                   created_by_ref: Option[Identifier] = None,
                                   custom: Option[CustomProps] = None,
                                   spec_version: String = SPEC_VERSION) extends SRO {

  def this(source_ref: Identifier, relationship_type: String, target_ref: Identifier) =
    this(Relationship.`type`, Identifier(Relationship.`type`), Timestamp.now(), Timestamp.now(),
      source_ref, relationship_type, target_ref)
}

object Relationship {
  val `type` = "relationship"
}

/**
  * A Sighting denotes the belief that something in CTI (e.g., an indicator, malware, tool, threat actor, etc.) was seen.
  */
@JsonCodec case class Sighting(`type`: String = Sighting.`type`,
                               id: Identifier = Identifier(Sighting.`type`),
                               created: Timestamp = Timestamp.now(),
                               modified: Timestamp = Timestamp.now(),
                               sighting_of_ref: Identifier,
                               first_seen: Option[Timestamp] = None,
                               last_seen: Option[Timestamp] = None,
                               count: Option[Long] = None,
                               observed_data_refs: Option[List[Identifier]] = None,
                               where_sighted_refs: Option[List[Identifier]] = None,
                               summary: Option[Boolean] = None,
                               description: Option[String] = None,
                               revoked: Option[Boolean] = None,
                               labels: Option[List[String]] = None,
                               external_references: Option[List[ExternalReference]] = None,
                               object_marking_refs: Option[List[Identifier]] = None,
                               granular_markings: Option[List[GranularMarking]] = None,
                               created_by_ref: Option[Identifier] = None,
                               custom: Option[CustomProps] = None,
                               spec_version: String = SPEC_VERSION) extends SRO

object Sighting {
  val `type` = "sighting"
}


//-----------------------------------------------------------------------
//------------------Language content---> this is not part of STIX-2.0
//-----------------------------------------------------------------------

/**
  * The language-content object represents text content for STIX Objects represented in other languages.
  */
@JsonCodec case class LanguageContent(`type`: String = LanguageContent.`type`,
                                      id: Identifier = Identifier(LanguageContent.`type`),
                                      created: Timestamp = Timestamp.now(),
                                      modified: Timestamp = Timestamp.now(),
                                      object_modified: Timestamp = Timestamp.now(),
                                      object_ref: Identifier,
                                      contents: Map[String, Map[String, String]], // todo <-- RFC5646_LANGUAGE_TAG and List and object
                                      created_by_ref: Option[Identifier] = None,
                                      revoked: Option[Boolean] = None,
                                      labels: Option[List[String]] = None,
                                      external_references: Option[List[ExternalReference]] = None,
                                      object_marking_refs: Option[List[Identifier]] = None,
                                      granular_markings: Option[List[GranularMarking]] = None,
                                      custom: Option[CustomProps] = None,
                                      spec_version: String = SPEC_VERSION) extends StixObj

object LanguageContent {
  val `type` = "language-content"
}
