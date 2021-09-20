package com.kodekutters.stix

import io.circe.generic.JsonCodec
import CustomCodecs._


/**
  * STIX-2.0 Extensions
  *
  * reference: https://oasis-open.github.io/cti-documentation/
  *
  * Author: R. Wathelet 2017
  */


/**
  * represents a Predefined Cyber Observable Object Extension.
  * To create a Custom Object Extension, simply extends this trait
  */
@JsonCodec sealed trait Extension

/**
  * a generic unknown custom extension object
  */
@JsonCodec case class CustomExtension(custom: Option[CustomProps] = None) extends Extension

object CustomExtension {
  val `type` = "custom_ext"
}

/**
  * The Archive File extension specifies a default extension for capturing properties specific to archive files.
  */
@JsonCodec case class ArchiveFileExt(contains_refs: Option[List[String]] = None,
                          version: Option[String] = None,
                          comment: Option[String] = None) extends Extension

object ArchiveFileExt {
  val `type` = "archive-ext"
}

/**
  * The Alternate Data Stream type represents an NTFS alternate data stream.
  */
@JsonCodec case class AlternateDataStream(name: String,
                               hashes: Option[Map[String, String]] = None,
                               size: Option[Long] = None)

/**
  * The NTFS file extension specifies a default extension for capturing properties specific to the storage of the file on the NTFS file system.
  */
@JsonCodec case class NTFSFileExt(sid: Option[String] = None,
                       alternate_data_streams: Option[List[AlternateDataStream]] = None) extends Extension

object NTFSFileExt {
  val `type` = "ntfs-ext"
}

/**
  * The PDF file extension specifies a default extension for capturing properties specific to PDF files.
  */
@JsonCodec case class PdfFileExt(version: Option[String] = None,
                      is_optimized: Option[Boolean] = None,
                      document_info_dict: Option[Map[String, String]] = None,
                      pdfid0: Option[String] = None,
                      pdfid1: Option[String] = None) extends Extension

object PdfFileExt {
  val `type` = "pdf-ext"
}

/**
  * The Raster Image file extension specifies a default extension for capturing properties specific to raster image files.
  */
@JsonCodec case class RasterImgExt(image_height: Option[Long] = None,
                        image_width: Option[Long] = None,
                        bits_per_pixel: Option[Long] = None,
                        image_compression_algorithm: Option[String] = None,
                        exif_tags: Option[Map[String, Either[Long, String]]] = None) extends Extension

object RasterImgExt {
  val `type` = "raster-image-ext"
}

/**
  * The Windows PE Optional Header type represents the properties of the PE optional header.
  */
@JsonCodec case class WindowPEOptionalHeaderType(magic_hex: Option[String] = None,
                                      major_linker_version: Option[Long] = None,
                                      minor_linker_version: Option[Long] = None,
                                      size_of_code: Option[Long] = None,
                                      size_of_initialized_data: Option[Long] = None,
                                      size_of_uninitialized_data: Option[Long] = None,
                                      address_of_entry_point: Option[Long] = None,
                                      base_of_code: Option[Long] = None,
                                      base_of_data: Option[Long] = None,
                                      image_base: Option[Long] = None,
                                      section_alignment: Option[Long] = None,
                                      file_alignment: Option[Long] = None,
                                      major_os_version: Option[Long] = None,
                                      minor_os_version: Option[Long] = None,
                                      major_image_version: Option[Long] = None,
                                      minor_image_version: Option[Long] = None,
                                      major_subsystem_version: Option[Long] = None,
                                      minor_subsystem_version: Option[Long] = None,
                                      win32_version_value_hex: Option[String] = None,
                                      size_of_image: Option[Long] = None,
                                      size_of_headers: Option[Long] = None,
                                      checksum_hex: Option[String] = None,
                                      dll_characteristics_hex: Option[String] = None,
                                      size_of_stack_reserve: Option[Long] = None,
                                      size_of_stack_commit: Option[Long] = None,
                                      size_of_heap_reserve: Option[Long] = None,
                                      size_of_heap_commit: Option[Long] = None,
                                      loader_flags_hex: Option[String] = None,
                                      number_of_rva_and_sizes: Option[Long] = None,
                                      hashes: Option[Map[String, String]] = None)

/**
  * The Windows PE Section type specifies metadata about a PE file section.
  */
@JsonCodec case class WindowPESectionType(name: String,
                               size: Option[Long] = None,
                               entropy: Option[Float] = None,
                               hashes: Option[Map[String, String]] = None)


/**
  * The Windows™ PE Binary File extension specifies a default extension for capturing properties specific to Windows portable executable (PE) files.
  */
@JsonCodec case class WindowPEBinExt(pe_type: String,
                          imphash: Option[String] = None,
                          machine_hex: Option[String] = None,
                          number_of_sections: Option[Long] = None,
                          time_date_stamp: Option[Timestamp] = None,
                          pointer_to_symbol_table_hex: Option[String] = None,
                          number_of_symbols: Option[Long] = None,
                          size_of_optional_header: Option[Long] = None,
                          characteristics_hex: Option[String] = None,
                          file_header_hashes: Option[Map[String, String]] = None,
                          optional_header: Option[WindowPEOptionalHeaderType] = None,
                          sections: Option[List[WindowPESectionType]] = None) extends Extension

object WindowPEBinExt {
  val `type` = "windows-pebinary-ext"
}


/**
  * The HTTP request extension specifies a default extension for capturing network traffic properties specific to HTTP requests.
  */
@JsonCodec case class HttpRequestExt(request_method: String,
                          request_value: String,
                          request_version: Option[String] = None,
                          request_header: Option[Map[String, String]] = None,
                          message_body_length: Option[Long] = None,
                          message_body_data_ref: Option[String] = None) extends Extension

object HttpRequestExt {
  val `type` = "http-request-ext"
}

/**
  * The ICMP extension specifies a default extension for capturing network traffic properties specific to ICMP.
  */
@JsonCodec case class ICMPExt(icmp_type_hex: String,
                   icmp_code_hex: String) extends Extension

object ICMPExt {
  val `type` = "icmp-ext"
}

/**
  * The TCP extension specifies a default extension for capturing network traffic properties specific to TCP.
  */
@JsonCodec case class TCPExt(src_flags_hex: Option[String] = None,
                  dst_flags_hex: Option[String] = None) extends Extension

object TCPExt {
  val `type` = "tcp-ext"
}

/**
  * The Network Socket extension specifies a default extension for capturing network traffic properties associated with network sockets.
  */
@JsonCodec case class SocketExt(address_family: String,
                     is_blocking: Option[Boolean] = None,
                     is_listening: Option[Boolean] = None,
                     protocol_family: Option[String] = None,
                     options: Option[Map[String, String]] = None,
                     socket_type: Option[String] = None,
                     socket_descriptor: Option[Long] = None,
                     socket_handle: Option[Long] = None) extends Extension

object SocketExt {
  val `type` = "socket-ext"
}

/**
  * The Windows Process extension specifies a default extension for capturing properties specific to Windows processes.
  */
@JsonCodec case class WindowsProcessExt(aslr_enabled: Option[Boolean] = None,
                             dep_enabled: Option[Boolean] = None,
                             priority: Option[String] = None,
                             owner_sid: Option[String] = None,
                             window_title: Option[String] = None,
                             startup_info: Option[Map[String, String]] = None) extends Extension

object WindowsProcessExt {
  val `type` = "windows-process-ext"
}

/**
  * The Windows Service extension specifies a default extension for capturing properties specific to Windows services.
  */
@JsonCodec case class WindowsServiceExt(service_name: String,
                             descriptions: Option[List[String]] = None,
                             display_name: Option[String] = None,
                             group_name: Option[String] = None,
                             start_type: Option[String] = None,
                             service_dll_refs: Option[List[String]] = None,
                             service_type: Option[String] = None,
                             service_status: Option[String] = None) extends Extension

object WindowsServiceExt {
  val `type` = "windows-service-ext"
}

/**
  * The UNIX account extension specifies a default extension for capturing the additional information for an account on a UNIX system.
  */
@JsonCodec case class UnixAccountExt(gid: Option[Long] = None,
                          groups: Option[List[String]] = None,
                          home_dir: Option[String] = None,
                          shell: Option[String] = None) extends Extension

object UnixAccountExt {
  val `type` = "unix-account-ext"
}

/**
  * The X.509 v3 Extensions type captures properties associated with X.509 v3 extensions, which serve as a mechanism for
  * specifying additional information such as alternative subject names.
  */
@JsonCodec case class X509V3Ext(basic_constraints: Option[String] = None,
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
                     private_key_usage_period_not_after: Option[Timestamp] = None,
                     certificate_policies: Option[String] = None,
                     policy_mappings: Option[String] = None) extends Extension

object X509V3Ext {
  val `type` = "x509-v3-extensions-type"
}

/**
  * represents a list of Predefined Cyber Observable Object Extension.
  */
@JsonCodec case class Extensions(extensions: Map[String, Extension])



