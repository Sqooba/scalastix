package com.kodekutters

import com.kodekutters.stix.Vocabularies.{RelationshipTypes, ReportTypes}
import com.kodekutters.stix._
import io.circe._
import io.circe.syntax._
import io.circe.literal._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class SerializationSpec extends AnyFlatSpec with Matchers {
  behavior of "serialization"

  it should "serialize and deserialize an artifact" in {
    val artifact = Artifact(mime_type = Some("application/json"), url = Some("https://google.com"), hashes = Some(Map("MD5" -> "123123")))
    val serialized = artifact.asJson.spaces2SortKeys
    val deserialized = parser.decode[Artifact](serialized).fold(throw _, identity)

    deserialized shouldBe artifact
  }

  it should "serialize and deserialize a bundle" in {
    val report = Report(name = "test", published = Timestamp.now(), object_refs = List(), report_types = Some(List(ReportTypes.MALWARE)))
    val markingDefinition = MarkingDefinition(definition_type = "test", definition = StatementMarking("something cool"))
    val relation = Relationship(source_ref = report.id, target_ref = markingDefinition.id, relationship_type = RelationshipTypes.RELATED_TO)
    val bundle = Bundle().add(report).add(markingDefinition).add(relation)

    val serialized = bundle.asJson.noSpaces
    val deserialized = parser.decode[Bundle](serialized).fold(throw _, identity)
    deserialized shouldBe bundle
  }

  it should "serialize as valid STIX" in new Fixture {
    report.asJson.deepDropNullValues shouldBe validReport
  }

  it should "correctly serialize files" in new Fixture {
    file.asJson.deepDropNullValues shouldBe validFile
  }

  it should "correctly serialize ipv4 address" in new Fixture {
    ip.asJson.deepDropNullValues shouldBe validIpv4
  }

  it should "correctly serialize domain name" in new Fixture {
    domain.asJson.deepDropNullValues shouldBe validDomain
  }

  it should "correctly serialize a bundle" in new Fixture {
    val validRelation =
      json"""{
            "type": "relationship",
            "spec_version": "2.1",
            "id": "relationship--240ac67b-5278-492e-88fa-e9c3e176561a",
            "created": "2016-01-20T12:31:12Z",
            "modified": "2016-01-20T12:31:12Z",
            "source_ref": "report--1358da6f-719c-42b2-aff3-df8df37af59a",
            "relationship_type": "related-to",
            "target_ref": "file--e70014d8-7dd9-5bac-9089-b6deb0472cf0"
      }"""
    val relation = Relationship(
      id = Identifier("relationship", "240ac67b-5278-492e-88fa-e9c3e176561a"),
      created = timestamp,
      modified = timestamp,
      source_ref = report.id,
      target_ref = file.id,
      relationship_type = "related-to"
    )
    val validBundle = Json.obj(
      "type" -> Json.fromString("bundle"),
      "id" -> Json.fromString("bundle--1358da6f-719c-42b2-aff3-df8df37af59b"),
      "objects" -> Json.arr(validReport, validFile, validRelation)
    )
    val bundle = Bundle(
      id = Identifier("bundle", "1358da6f-719c-42b2-aff3-df8df37af59b"),
      objects = Seq(
        report.asJson.deepDropNullValues,
        file.asJson.deepDropNullValues,
        relation.asJson.deepDropNullValues
      )
    )

    bundle.asJson.deepDropNullValues shouldBe validBundle
  }

  trait Fixture {
    val timestamp = Timestamp("2016-01-20T12:31:12Z")
    val validReport: Json =
      json"""
            {
                "type": "report",
                "id": "report--1358da6f-719c-42b2-aff3-df8df37af59a",
                "created": "2016-01-20T12:31:12Z",
                "lang": "en",
                "modified": "2016-01-20T12:31:12Z",
                "name": "analysis id 1",
                "description": "alpha",
                "report_types": ["malware"],
                "created_by_ref": "identity--30038539-3eb6-44bc-a59e-d0d3fe8469ee",
                "published": "2016-01-20T12:31:12Z",
                "labels": ["label"],
                "object_refs": ["file--30038539-3eb6-44bc-a59e-d0d3fe84695a"],
                "object_marking_refs": ["marking-definition--613f2e26-407d-48c7-9eca-b8e91df99dc9"],
                "spec_version": "2.1",
                "external_references": [
                    {"source_name": "src", "url": "https://example.com", "external_id": "1"}
                ]
            }
            """
    val report: Report =
      Report(
        id = Identifier("report", "1358da6f-719c-42b2-aff3-df8df37af59a"),
        name = "analysis id 1",
        description = Some("alpha"),
        report_types = Some(List("malware")),
        created_by_ref = Some(Identifier("identity", "30038539-3eb6-44bc-a59e-d0d3fe8469ee")),
        published = timestamp,
        created = timestamp,
        modified = timestamp,
        lang = Some("en"),
        labels = Some(List("label")),
        object_refs = List(Identifier("file", "30038539-3eb6-44bc-a59e-d0d3fe84695a")),
        object_marking_refs = Some(List(TLP.WHITE)),
        external_references = Some(List(ExternalReference(source_name = "src", url = Some("https://example.com"), external_id = Some("1"))))
      )

    val validFile: Json =
      json"""{
            "hashes": {"SHA-256": "841498c9b2855e357baf22af125bffa6e512c20ffa1cd30752da3bc4c3e540e4"},
            "size": 256,
            "name": "Docker",
            "type": "file",
            "spec_version": "2.1",
            "id": "file--e70014d8-7dd9-5bac-9089-b6deb0472cf0"
      }"""
    val file: File = File(
      id = Identifier("file","e70014d8-7dd9-5bac-9089-b6deb0472cf0"),
      size = Some(256),
      name = Some("Docker"),
      hashes = Some(Map("SHA-256" -> "841498c9b2855e357baf22af125bffa6e512c20ffa1cd30752da3bc4c3e540e4"))
    )

    val validIpv4 = json"""{"value": "127.0.0.1", "type": "ipv4-addr", "spec_version": "2.1", "id": "ipv4-addr--679c6c82-b4be-52e2-9c7a-198689f6f77b"}"""
    val ip: IPv4Address = IPv4Address(
      id = Identifier("ipv4-addr", "679c6c82-b4be-52e2-9c7a-198689f6f77b"),
      value = "127.0.0.1"
    )
    val validDomain = json"""{"value": "example.com", "type": "domain-name", "spec_version": "2.1", "id": "domain-name--bedb4899-d24b-5401-bc86-8f6b4cc18ec7"}"""
    val domain: DomainName = DomainName(
      id = Identifier("domain-name", "bedb4899-d24b-5401-bc86-8f6b4cc18ec7"),
      value = "example.com"
    )
  }

}
