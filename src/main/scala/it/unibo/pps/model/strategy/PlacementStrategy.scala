package it.unibo.pps.model.strategy

import it.unibo.pps.model.board.Board
import it.unibo.pps.utils.{Color, Position}

import scala.util.Random

trait PlacementStrategy[-C, O]:
  def computePlacement(using context: C): O

case class UserPlacementStrategy() extends PlacementStrategy[Position, Position]:
  def computePlacement(using userChoice: Position): Position = userChoice

sealed trait OpponentPlacementStrategy extends PlacementStrategy[Board, Position]

case class RandomPlacementStrategy(color: Color) extends OpponentPlacementStrategy:
  def computePlacement(using board: Board): Position =
    val availablePlacements = board.getAvailablePlacements(color)
    if availablePlacements.isEmpty then throw IllegalStateException("Opponent has no available placements")
    val randomIndex = Random.nextInt(availablePlacements.size)
    availablePlacements.toSeq(randomIndex)