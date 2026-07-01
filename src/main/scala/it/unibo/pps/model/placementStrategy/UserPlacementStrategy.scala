package it.unibo.pps.model.placementStrategy

import it.unibo.pps.utils.Position

case class UserPlacementStrategy() extends PlacementStrategy[Position, Position]:
  override def computePlacement(using userChoice: Position): Position = userChoice

