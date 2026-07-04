package it.unibo.pps.model.placementStrategy

import it.unibo.pps.utils.Position
import it.unibo.pps.model.MatchState

abstract case class OpponentPlacementStrategy() extends PlacementStrategy[MatchState, Position]:
  def computePlacement(using matchState: MatchState): Position
