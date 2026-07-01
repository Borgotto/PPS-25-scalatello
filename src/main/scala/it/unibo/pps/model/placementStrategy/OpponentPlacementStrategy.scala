package it.unibo.pps.model.placementStrategy

import it.unibo.pps.model.MatchState
import it.unibo.pps.utils.Position

case class OpponentPlacementStrategy() extends PlacementStrategy[MatchState, Position]:
  override def computePlacement(using matchState: MatchState): Position =
    Position(0, 0) // Placeholder implementation
