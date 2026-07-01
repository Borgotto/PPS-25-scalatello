package it.unibo.pps.model.player

import it.unibo.pps.utils.Color
import it.unibo.pps.model.placementStrategy.PlacementStrategy

trait Player:
  def color: Color
  def getPlacementStrategy: PlacementStrategy[_, _]
