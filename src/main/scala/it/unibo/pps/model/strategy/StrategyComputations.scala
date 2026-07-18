package it.unibo.pps.model.strategy

import it.unibo.pps.domain.{Color, Position}
import it.unibo.pps.model.board.Board

import scala.util.boundary, boundary.break
import scala.math.max
import scala.collection.parallel.CollectionConverters.*

object StrategyComputations:
  extension (board: Board)
    private def unsafePlaceDisk(color: Color, position: Position): Board =
      board.placeDisk(color, position, validatePosition = false)

    private def weight(position: Position): Int =
      val maxRow = board.state.shape.maxRow
      val maxCol = board.state.shape.maxColumn
      val row = position.row
      val col = position.column
      if
        // Corners: Highly prized
        (row == 0 || row == maxRow) &&
        (col == 0 || col == maxCol)
        then 10
      else if
        // Inner corners: Dangerous
        (row == 1 || row == maxRow - 1) &&
        (col == 1 || col == maxCol - 1)
        then -4
      else if 
        // Edges: Defensive positions
        row == 0 || row == maxRow ||
        col == 0 || col == maxCol
        then 2
      else 1

    private def score(color: Color): Int =
      board.state.disks.map(disk =>
        val diskScore = if disk.color.equals(color) then 1 else -1
        val positionScore = board.weight(disk.position)
        diskScore + positionScore
      ).sum

  private def negamax(board: Board, depth: Int, color: Color)
                     (using alpha: Int = Int.MinValue + 1, beta: Int = Int.MaxValue): Int =
    val availablePlacements = board.getAvailablePlacements(color)
    val nodeIsTerminal = availablePlacements.isEmpty
    
    if depth == 0 || nodeIsTerminal then
      board.score(color)
    else
      boundary: // boundary for pruning
        availablePlacements.foldLeft(alpha): (currentAlpha, position) =>
          val newBoard = board.unsafePlaceDisk(color, position)
          val value = -negamax(newBoard, depth - 1, color.opposite)(using -beta, -currentAlpha)
          if value >= beta then
            break(value) // stop searching this branch
          max(value, currentAlpha)

  def calculateBestPlacement(color: Color, depth: Int = 1)
                            (using board: Board): Position =
    val availablePlacements = board.getAvailablePlacements(color).par
    val bestPlacement = availablePlacements.minBy: position =>
      val newBoard = board.unsafePlaceDisk(color, position)
      negamax(newBoard, depth - 1, color.opposite)
    bestPlacement
