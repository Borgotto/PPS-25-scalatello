package it.unibo.pps.model.player

import it.unibo.pps.utils.Color
import it.unibo.pps.model.placementStrategy.{OpponentPlacementStrategy, RandomOpponentPlacementStrategy}

enum Opponent extends Player:
  val color: Color = Color.White
  case RandomOpponent
    def placementStrategy: OpponentPlacementStrategy = RandomOpponentPlacementStrategy()
