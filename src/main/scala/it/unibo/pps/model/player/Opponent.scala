package it.unibo.pps.model.player

import it.unibo.pps.utils.Color
import it.unibo.pps.model.placementStrategy.*

enum Opponent extends Player:
  val color: Color = Color.White
  case RandomOpponent
    override def getPlacementStrategy: OpponentPlacementStrategy = RandomOpponentPlacementStrategy()

