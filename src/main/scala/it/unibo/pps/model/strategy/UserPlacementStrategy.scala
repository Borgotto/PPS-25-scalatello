package it.unibo.pps.model.strategy

import it.unibo.pps.utils.Position

case class UserPlacementStrategy() extends PlacementStrategy[Position, Position]:
  override def computePlacement(using userChoice: Position): Position = userChoice
