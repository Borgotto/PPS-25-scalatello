package it.unibo.pps.model.strategy

import it.unibo.pps.domain.{Color, Position}
import it.unibo.pps.model.board.Board

import upickle.default.ReadWriter

import scala.util.Random

trait PlacementStrategy[-C, O]:
  def computePlacement(using context: C): O

case class UserPlacementStrategy() extends PlacementStrategy[Position, Position] derives ReadWriter:
  def computePlacement(using userChoice: Position): Position = userChoice

sealed trait OpponentPlacementStrategy extends PlacementStrategy[Board, Position] derives ReadWriter

case class RandomPlacementStrategy(color: Color) extends OpponentPlacementStrategy:
  def computePlacement(using board: Board): Position =
    val availablePlacements = board.getAvailablePlacements(color)
    if availablePlacements.isEmpty then throw IllegalStateException("Opponent has no available placements")
    val randomIndex = Random.nextInt(availablePlacements.size)
    availablePlacements.toSeq(randomIndex)

case class SmartPlacementStrategy(color: Color, depth: Int) extends OpponentPlacementStrategy:
  def computePlacement(using board: Board): Position =
    StrategyComputations.calculateBestPlacement(color, depth)
