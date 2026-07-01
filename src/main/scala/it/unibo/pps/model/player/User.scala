package it.unibo.pps.model.player

import it.unibo.pps.utils.Color
import it.unibo.pps.model.placementStrategy.UserPlacementStrategy

case class User() extends Player:
  override def color: Color = Color.Black
  override def getPlacementStrategy: UserPlacementStrategy = UserPlacementStrategy()
