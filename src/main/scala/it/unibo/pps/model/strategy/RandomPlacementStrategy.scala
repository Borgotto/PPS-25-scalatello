package it.unibo.pps.model.strategy

import it.unibo.pps.model.board.Board
import it.unibo.pps.model.strategy.OpponentPlacementStrategy
import it.unibo.pps.utils.{Color, Position}

import scala.util.Random

class RandomPlacementStrategy(color: Color) extends OpponentPlacementStrategy:
  def computePlacement(using board: Board): Position =
    val availablePlacements = board.getAvailablePlacements(color)
    if availablePlacements.isEmpty then throw IllegalStateException("Opponent has no available placements")
    val randomIndex = Random.nextInt(availablePlacements.size)
    availablePlacements.toSeq(randomIndex)
