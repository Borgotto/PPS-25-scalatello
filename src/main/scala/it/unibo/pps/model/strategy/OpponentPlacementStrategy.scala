package it.unibo.pps.model.strategy

import it.unibo.pps.state.MatchState
import it.unibo.pps.utils.Position

abstract case class OpponentPlacementStrategy() extends PlacementStrategy[MatchState, Position]:
  override def computePlacement(using matchState: MatchState): Position
