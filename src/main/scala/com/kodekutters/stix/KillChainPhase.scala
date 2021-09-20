package com.kodekutters.stix

import io.circe.generic.JsonCodec

/**
  * The kill-chain-phase represents a phase in a kill chain, which describes the various phases
  * an attacker may undertake in order to achieve their objectives.
  */
@JsonCodec case class KillChainPhase(kill_chain_name: String, phase_name: String) {
  val `type` = KillChainPhase.`type`

  override def toString = kill_chain_name + "," + phase_name
}

object KillChainPhase {
  val `type` = "kill-chain-phase"
}

