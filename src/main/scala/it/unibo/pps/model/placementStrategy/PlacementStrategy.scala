package it.unibo.pps.model.placementStrategy

trait PlacementStrategy[-C, O]:
  def computePlacement(using context: C): O