package it.unibo.pps.model.strategy

import it.unibo.pps.model.board.Board
import it.unibo.pps.utils.{Color, Position}

import scala.util.Random

case class RandomPlacementStrategy(color: Color) extends PlacementStrategy[Board, Position]:
  override def computePlacement(using board: Board): Position =
    val availablePlacements = board.getAvailablePlacements(color)
    if availablePlacements.isEmpty then throw IllegalStateException("Opponent has no available placements")
    val randomIndex = Random.nextInt(availablePlacements.size)
    availablePlacements.toSeq(randomIndex)
