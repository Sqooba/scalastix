package com.kodekutters.stix

object Vocabularies {

  object ReportTypes {
    val ATTACK_PATTERN = "attack-pattern"
    val CAMPAIGN = "campaign"
    val IDENTITY = "identity"
    val INDICATOR = "indicator"
    val INTRUSION_SET = "intrusion-set"
    val MALWARE = "malware"
    val OBSERVED_DATA = "observed-data"
    val THREAT_ACTOR = "threat-actor"
    val THREAT_REPORT = "threat-report"
    val TOOL = "tool"
    val VULNERABILITY = "vulnerability"
  }

  object RelationshipTypes {
    val USES = "uses"
    val TARGETS = "targets"
    val INDICATES = "indicates"
    val MITIGATES = "mitigates"
    val ATTRIBUTED_TO = "attributed-to"
    val VARIANT_OF = "variant-of"
    val DUPLICATE_OF = "duplicate-of"
    val DERIVED_FROM = "derived-from"
    val RELATED_TO = "related-to"
    val IMPERSONATES = "impersonates"
  }

}

