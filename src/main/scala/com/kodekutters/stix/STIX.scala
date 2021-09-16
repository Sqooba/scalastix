package com.kodekutters.stix

trait STIX {
  def `type`: String
  def id: Identifier
  def spec_version: String
}
