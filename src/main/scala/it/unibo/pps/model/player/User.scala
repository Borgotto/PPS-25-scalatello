package it.unibo.pps.model.player

import it.unibo.pps.utils.Color
import it.unibo.pps.model.placementStrategy.UserPlacementStrategy

case class User():
  def color: Color = Color.Black
  def getPlacementStrategy: UserPlacementStrategy = UserPlacementStrategy()
