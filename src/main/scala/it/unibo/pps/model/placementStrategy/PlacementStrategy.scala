package it.unibo.pps.model.placementStrategy

import it.unibo.pps.utils.Position
import it.unibo.pps.model.MatchState

trait PlacementStrategy[-C, O]:
  def computePlacement(using context: C): O

case class UserPlacementStrategy() extends PlacementStrategy[Position, Position]:
  def computePlacement(using userChoice: Position): Position = userChoice

abstract case class OpponentPlacementStrategy() extends PlacementStrategy[MatchState, Position]:
  def computePlacement(using matchState: MatchState): Position