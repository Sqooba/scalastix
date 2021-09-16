package com.kodekutters.stix

import io.circe.generic.JsonCodec
import CustomCodecs._

/**
  * STIX-2.0 protocol, Cyber Observable Objects
  *
  * STIX Cyber Observables document the facts concerning what happened on a network or host,
  * but not necessarily the who or when, and never the why.
  *
  * reference: https://oasis-open.github.io/cti-documentation/
  *
  * Author: R. Wathelet 2017
  *
  */

/**
  * common properties of Observables
  * To create a Custom Observable, simply extends this trait
  */
@JsonCodec sealed trait Observable extends STIX {
  def extensions: Option[Extensions]
  def custom: Option[CustomProps]
}

/**
  * The Artifact Object permits capturing an array of bytes (8-bits), as a base64-encoded string,
  * or linking to a file-like payload.
  */
@JsonCodec case class Artifact(`type`: String = Artifact.`type`,
                               id: Identifier = Identifier(Artifact.`type`),
                               mime_type: Option[String] = None,
                               payload_bin: Option[String] = None, // base64-encoded string
                               url: Option[String] = None,
                               hashes: Option[Map[String, String]] = None,
                               extensions: Option[Extensions] = None,
                               custom: Option[CustomProps] = None,
                               spec_version: String = SPEC_VERSION) extends Observable {

}

object Artifact {
  val `type` = "artifact"
}

/**
  * represents the properties of an Autonomous System (AS).
  */
@JsonCodec case class AutonomousSystem(`type`: String = AutonomousSystem.`type`,
                                       id: Identifier = Identifier(AutonomousSystem.`type`),
                                       number: Long,
                                       name: Option[String] = None,
                                       rir: Option[String] = None,
                                       extensions: Option[Extensions] = None,
                                       custom: Option[CustomProps] = None,
                                       spec_version: String = SPEC_VERSION) extends Observable

object AutonomousSystem {
  val `type` = "autonomous-system"
}

/**
  * The Directory Object represents the properties common to a file system directory.
  */
@JsonCodec case class Directory(`type`: String = Directory.`type`,
                                id: Identifier = Identifier(Directory.`type`),
                                path: String,
                                path_enc: Option[String] = None,
                                created: Option[Timestamp] = None,
                                modified: Option[Timestamp] = None,
                                accessed: Option[Timestamp] = None,
                                contains_refs: Option[List[String]] = None, // todo object-ref must be file or directory type
                                extensions: Option[Extensions] = None,
                                custom: Option[CustomProps] = None,
                                spec_version: String = SPEC_VERSION) extends Observable

object Directory {
  val `type` = "directory"
}

/**
  * The Domain Name represents the properties of a network domain name.
  */
@JsonCodec case class DomainName(`type`: String = DomainName.`type`,
                                 id: Identifier = Identifier(DomainName.`type`),
                                 value: String,
                                 resolves_to_refs: Option[List[String]] = None, // todo object-ref must be ipv4-addr or ipv6-addr or domain-name
                                 extensions: Option[Extensions] = None,
                                 custom: Option[CustomProps] = None,
                                 spec_version: String = SPEC_VERSION) extends Observable

object DomainName {
  val `type` = "domain-name"
}

/**
  * The Email Address Object represents a single email address.
  */
@JsonCodec case class EmailAddress(`type`: String = EmailAddress.`type`,
                                   id: Identifier = Identifier(EmailAddress.`type`),
                                   value: String,
                                   display_name: Option[String] = None,
                                   belongs_to_ref: Option[String] = None, // todo  must be of type user-account
                                   extensions: Option[Extensions] = None,
                                   custom: Option[CustomProps] = None,
                                   spec_version: String = SPEC_VERSION) extends Observable

object EmailAddress {
  val `type` = "email-addr"
}

/**
  * Specifies one component of a multi-part email body.
  */
@JsonCodec case class EmailMimeType(body: Option[String] = None,
                         body_raw_ref: Option[String] = None, // todo must be of type artifact or file.
                         content_type: Option[String] = None,
                         content_disposition: Option[String] = None,
                         custom: Option[CustomProps] = None)

/**
  * The Email Message Object represents an instance of an email message, corresponding to the internet message format
  * described in [RFC5322] and related RFCs.
  */
@JsonCodec case class EmailMessage(`type`: String = EmailMessage.`type`,
                                   id: Identifier = Identifier(EmailAddress.`type`),
                                   is_multipart: Boolean,
                                   body_multipart: Option[List[EmailMimeType]] = None, // todo mime-part-type and only if is_multipart=true
                                   body: Option[String] = None, // todo only if is_multipart=false
                                   date: Option[Timestamp] = None,
                                   content_type: Option[String] = None,
                                   from_ref: Option[String] = None, // todo must be of type email-address
                                   sender_ref: Option[String] = None, // todo must be of type email-address
                                   to_refs: Option[List[String]] = None, // todo must be of type email-address
                                   cc_refs: Option[List[String]] = None, // todo must be of type email-address
                                   bcc_refs: Option[List[String]] = None, // todo must be of type email-address
                                   subject: Option[String] = None,
                                   received_lines: Option[List[String]] = None,
                                   additional_header_fields: Option[Map[String, String]] = None,
                                   raw_email_ref: Option[String] = None, // todo must be of type artifact
                                   extensions:  Option[Extensions] = None,
                                   custom: Option[CustomProps] = None,
                                   spec_version: String = SPEC_VERSION) extends Observable

object EmailMessage {
  val `type` = "email-message"
}

/**
  * The File Object represents the properties of a file.
  */
@JsonCodec case class File(`type`: String = File.`type`,
                           id: Identifier = Identifier(File.`type`),
                           hashes: Option[Map[String, String]] = None,
                           size: Option[Long] = None,
                           name: Option[String] = None,
                           name_enc: Option[String] = None,
                           magic_number_hex: Option[String] = None, // hex
                           mime_type: Option[String] = None,
                           created: Option[Timestamp] = None,
                           modified: Option[Timestamp] = None,
                           accessed: Option[Timestamp] = None,
                           parent_directory_ref: Option[String] = None,
                           is_encrypted: Option[Boolean] = None,
                           encryption_algorithm: Option[String] = None,
                           decryption_key: Option[String] = None,
                           contains_refs: Option[List[String]] = None,
                           content_ref: Option[String] = None,
                           extensions:  Option[Extensions] = None,
                           custom: Option[CustomProps] = None,
                           spec_version: String = SPEC_VERSION) extends Observable

object File {
  val `type` = "file"
}

/**
  * The IPv4 Address Object represents one or more IPv4 addresses expressed using CIDR notation.
  */
@JsonCodec case class IPv4Address(`type`: String = IPv4Address.`type`,
                                  id: Identifier = Identifier(IPv4Address.`type`),
                                  value: String,
                                  resolves_to_refs: Option[List[String]] = None,
                                  belongs_to_refs: Option[List[String]] = None,
                                  extensions: Option[Extensions] = None,
                                  custom: Option[CustomProps] = None,
                                  spec_version: String = SPEC_VERSION) extends Observable

object IPv4Address {
  val `type` = "ipv4-addr"
}

/**
  * The IPv6 Address Object represents one or more IPv6 addresses expressed using CIDR notation.
  */
@JsonCodec case class IPv6Address(`type`: String = IPv6Address.`type`,
                                  id: Identifier = Identifier(IPv6Address.`type`),
                                  value: String,
                                  resolves_to_refs: Option[List[String]] = None,
                                  belongs_to_refs: Option[List[String]] = None,
                                  extensions: Option[Extensions] = None,
                                  custom: Option[CustomProps] = None,
                                  spec_version: String = SPEC_VERSION) extends Observable

object IPv6Address {
  val `type` = "ipv6-addr"
}

/**
  * The MAC Address Object represents a single Media Access Control (MAC) address.
  */
@JsonCodec case class MACAddress(`type`: String = MACAddress.`type`,
                                 id: Identifier = Identifier(MACAddress.`type`),
                                 value: String,
                                 extensions: Option[Extensions] = None,
                                 custom: Option[CustomProps] = None,
                                 spec_version: String = SPEC_VERSION) extends Observable

object MACAddress {
  val `type` = "mac-addr"
}

/**
  * The Mutex Object represents the properties of a mutual exclusion (mutex) object.
  */
@JsonCodec case class Mutex(`type`: String = Mutex.`type`,
                            id: Identifier = Identifier(Mutex.`type`),
                            name: String,
                            extensions: Option[Extensions] = None,
                            custom: Option[CustomProps] = None,
                            spec_version: String = SPEC_VERSION) extends Observable

object Mutex {
  val `type` = "mutex"
}

/**
  * The Network Traffic Object represents arbitrary network traffic that originates from a source and is addressed to a destination.
  */
@JsonCodec case class NetworkTraffic(`type`: String = NetworkTraffic.`type`,
                                     id: Identifier = Identifier(NetworkTraffic.`type`),
                                     start: Option[Timestamp] = None,
                                     end: Option[Timestamp] = None,
                                     is_active: Option[Boolean] = None,
                                     src_ref: Option[String] = None,
                                     dst_ref: Option[String] = None,
                                     src_port: Option[Int] = None,
                                     dst_port: Option[Int] = None,
                                     protocols: Option[List[String]] = None,
                                     src_byte_count: Option[Long] = None,
                                     dst_byte_count: Option[Long] = None,
                                     src_packets: Option[Long] = None,
                                     dst_packets: Option[Long] = None,
                                     ipfix: Option[Map[String, Either[Long, String]]] = None,
                                     src_payload_ref: Option[String] = None,
                                     dst_payload_ref: Option[String] = None,
                                     encapsulates_refs: Option[List[String]] = None,
                                     encapsulated_by_ref: Option[String] = None,
                                     extensions:  Option[Extensions] = None,
                                     custom: Option[CustomProps] = None,
                                     spec_version: String = SPEC_VERSION) extends Observable

object NetworkTraffic {
  val `type` = "network-traffic"
}

/**
  * The Process Object represents common properties of an instance of a computer program as executed on an operating system.
  */
@JsonCodec case class Process(`type`: String = Process.`type`,
                              id: Identifier = Identifier(Process.`type`),
                              is_hidden: Option[Boolean] = None,
                              pid: Option[Long] = None,
                              name: Option[String] = None,
                              created: Option[Timestamp] = None,
                              cwd: Option[String] = None,
                              arguments: Option[List[String]] = None,
                              command_line: Option[String] = None,
                              environment_variables: Option[Map[String, String]] = None,
                              opened_connection_refs: Option[List[String]] = None,
                              creator_user_ref: Option[String] = None,
                              binary_ref: Option[String] = None,
                              parent_ref: Option[String] = None,
                              child_refs: Option[List[String]] = None,
                              extensions: Option[Extensions] = None,
                              custom: Option[CustomProps] = None,
                              spec_version: String = SPEC_VERSION) extends Observable

object Process {
  val `type` = "process"
}

/**
  * The Software Object represents high-level properties associated with software, including software products.
  */
@JsonCodec case class Software(`type`: String = Software.`type`,
                               id: Identifier = Identifier(Software.`type`),
                               name: String,
                               cpe: Option[String] = None,
                               languages: Option[List[String]] = None,
                               vendor: Option[String] = None,
                               version: Option[String] = None,
                               extensions: Option[Extensions] = None,
                               custom: Option[CustomProps] = None,
                               spec_version: String = SPEC_VERSION) extends Observable

object Software {
  val `type` = "software"
}

/**
  * The URL Object represents the properties of a uniform resource locator (URL).
  */
@JsonCodec case class URL(`type`: String = URL.`type`,
                          id: Identifier = Identifier(URL.`type`),
                          value: String,
                          extensions: Option[Extensions] = None,
                          custom: Option[CustomProps] = None,
                          spec_version: String = SPEC_VERSION) extends Observable

object URL {
  val `type` = "url"
}

/**
  * The User Account Object represents an instance of any type of user account, including but not limited to operating system, device, messaging service, and social media platform accounts.
  */
@JsonCodec case class UserAccount(`type`: String = UserAccount.`type`,
                                  id: Identifier = Identifier(UserAccount.`type`),
                                  user_id: String,
                                  account_login: Option[String] = None,
                                  account_type: Option[String] = None,
                                  display_name: Option[String] = None,
                                  is_service_account: Option[Boolean] = None,
                                  is_privileged: Option[Boolean] = None,
                                  can_escalate_privs: Option[Boolean] = None,
                                  is_disabled: Option[Boolean] = None,
                                  account_created: Option[Timestamp] = None,
                                  account_expires: Option[Timestamp] = None,
                                  password_last_changed: Option[Timestamp] = None,
                                  account_first_login: Option[Timestamp] = None,
                                  account_last_login: Option[Timestamp] = None,
                                  extensions:  Option[Extensions] = None,
                                  custom: Option[CustomProps] = None,
                                  spec_version: String = SPEC_VERSION) extends Observable

object UserAccount {
  val `type` = "user-account"
}

/**
  * The Windows Registry Value type captures the properties of a Windows Registry Key Value.
  */
@JsonCodec case class WindowsRegistryValueType(name: String,
                                    data: Option[String] = None,
                                    data_type: Option[String] = None)

/**
  * The Registry Key Object represents the properties of a Windows registry key.
  */
@JsonCodec case class WindowsRegistryKey(`type`: String = WindowsRegistryKey.`type`,
                                         id: Identifier = Identifier(WindowsRegistryKey.`type`),
                                         key: String,
                                         values: Option[List[WindowsRegistryValueType]] = None,
                                         modified: Option[Timestamp] = None,
                                         creator_user_ref: Option[String] = None,
                                         number_of_subkeys: Option[Long] = None,
                                         extensions: Option[Extensions] = None,
                                         custom: Option[CustomProps] = None,
                                         spec_version: String = SPEC_VERSION) extends Observable

object WindowsRegistryKey {
  val `type` = "windows-registry-key"
}

/**
  * The X.509 v3 Extensions type captures properties associated with X.509 v3 extensions, which serve as a mechanism for specifying additional information such as alternative subject names.
  */
@JsonCodec case class X509V3ExtenstionsType(basic_constraints: Option[String] = None,
                                 name_constraints: Option[String] = None,
                                 policy_constraints: Option[String] = None,
                                 key_usage: Option[String] = None,
                                 extended_key_usage: Option[String] = None,
                                 subject_key_identifier: Option[String] = None,
                                 authority_key_identifier: Option[String] = None,
                                 subject_alternative_name: Option[String] = None,
                                 issuer_alternative_name: Option[String] = None,
                                 subject_directory_attributes: Option[String] = None,
                                 crl_distribution_points: Option[String] = None,
                                 inhibit_any_policy: Option[String] = None,
                                 private_key_usage_period_not_before: Option[Timestamp] = None,
                                 private_key_usage_period_not_after: Option[Timestamp] = None)

/**
  * The X.509 Certificate Object represents the properties of an X.509 certificate, as defined by ITU recommendation X.509 [X.509].
  */
@JsonCodec case class X509Certificate(`type`: String = X509Certificate.`type`,
                                      id: Identifier = Identifier(X509Certificate.`type`),
                                      is_self_signed: Option[Boolean] = None,
                                      hashes: Option[Map[String, String]] = None,
                                      version: Option[String] = None,
                                      serial_number: Option[String] = None,
                                      signature_algorithm: Option[String] = None,
                                      issuer: Option[String] = None,
                                      validity_not_before: Option[Timestamp] = None,
                                      validity_not_after: Option[Timestamp] = None,
                                      subject: Option[String] = None,
                                      subject_public_key_algorithm: Option[String] = None,
                                      subject_public_key_modulus: Option[String] = None,
                                      subject_public_key_exponent: Option[Long] = None,
                                      x509_v3_extensions: Option[X509V3ExtenstionsType] = None,
                                      extensions:  Option[Extensions] = None,
                                      custom: Option[CustomProps] = None,
                                      spec_version: String = SPEC_VERSION) extends Observable

object X509Certificate {
  val `type` = "x509-certificate"
}

/**
  * A generic custom observable object
  */
@JsonCodec case class CustomObservable(`type`: String = CustomObservable.`type`,
                                       id: Identifier = Identifier(CustomObservable.`type`),
                                       extensions: Option[Extensions] = None,
                                       custom: Option[CustomProps] = None,
                                       spec_version: String = SPEC_VERSION) extends Observable

object CustomObservable {
  val `type` = "x-custom-observable"
}

