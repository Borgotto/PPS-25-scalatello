package it.unibo.pps.model.player

import it.unibo.pps.utils.Color
import it.unibo.pps.model.placementStrategy.OpponentPlacementStrategy

case class Opponent() extends Player:
  override def color: Color = Color.White
  override def getPlacementStrategy: OpponentPlacementStrategy = OpponentPlacementStrategy()
