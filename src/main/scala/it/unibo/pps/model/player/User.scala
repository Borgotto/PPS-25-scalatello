package it.unibo.pps.model.player

import it.unibo.pps.utils.Color
import it.unibo.pps.model.placementStrategy.UserPlacementStrategy

case class User() extends Player:
  def color: Color = Color.Black
  def placementStrategy: UserPlacementStrategy = UserPlacementStrategy()
