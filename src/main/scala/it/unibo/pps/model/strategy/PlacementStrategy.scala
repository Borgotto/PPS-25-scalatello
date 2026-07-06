package it.unibo.pps.model.strategy

trait PlacementStrategy[-C, O]:
  def computePlacement(using context: C): O