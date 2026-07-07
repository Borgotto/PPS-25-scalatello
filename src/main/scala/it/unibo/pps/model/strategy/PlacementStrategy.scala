package it.unibo.pps.model.strategy

import it.unibo.pps.model.board.Board
import it.unibo.pps.utils.Position

trait PlacementStrategy[-C, O]:
  def computePlacement(using context: C): O

case class UserPlacementStrategy() extends PlacementStrategy[Position, Position]:
  def computePlacement(using userChoice: Position): Position = userChoice

abstract case class OpponentPlacementStrategy() extends PlacementStrategy[Board, Position]:
  def computePlacement(using board: Board): Position