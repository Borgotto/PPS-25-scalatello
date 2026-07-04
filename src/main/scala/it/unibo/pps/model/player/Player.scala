package it.unibo.pps.model.player

import it.unibo.pps.utils.Color
import it.unibo.pps.model.placementStrategy.{OpponentPlacementStrategy, UserPlacementStrategy}

trait Player:
  def color: Color
  def placementStrategy: UserPlacementStrategy | OpponentPlacementStrategy
