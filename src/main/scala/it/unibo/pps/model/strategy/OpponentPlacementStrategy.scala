package it.unibo.pps.model.strategy

import it.unibo.pps.model.board.Board
import it.unibo.pps.utils.Position

abstract case class OpponentPlacementStrategy() extends PlacementStrategy[Board, Position]:
  override def computePlacement(using board: Board): Position
