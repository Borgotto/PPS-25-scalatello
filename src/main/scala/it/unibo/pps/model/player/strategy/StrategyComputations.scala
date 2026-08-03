package it.unibo.pps.model.player.strategy

import it.unibo.pps.domain.{Color, Position}
import it.unibo.pps.model.board.Board
import it.unibo.pps.model.player.strategy.StrategyHelper.score

import scala.util.boundary
import boundary.break
import scala.math.max
import scala.collection.parallel.CollectionConverters.*

private object StrategyComputations:

  /**
   * @see [[https://en.wikipedia.org/wiki/Negamax Negamax algorithm]] for a detailed explanation of the algorithm.
   */
  private def negamax(board: Board, depth: Int, color: Color)
                     (using alpha: Int = Int.MinValue + 1, beta: Int = Int.MaxValue): Int =
    val availablePlacements = board.getAvailablePlacements(color)
    val nodeIsTerminal = availablePlacements.isEmpty
    
    if depth == 0 || nodeIsTerminal then
      board.score(color)
    else
      boundary: // boundary for pruning
        availablePlacements.foldLeft(alpha): (currentAlpha, position) =>
          val newBoard = board.placeDisk(color, position, validatePosition = false)
          val value = -negamax(newBoard, depth - 1, color.opposite)(using -beta, -currentAlpha)
          if value >= beta then
            break(value) // stop searching this branch
          max(value, currentAlpha)

  /**
   * Runs the negamax algorithm on all available placements and returns the best one for the given color
   */
  def calculateBestPlacement(color: Color, depth: Int)
                            (using board: Board): Position =
    val availablePlacements = board.getAvailablePlacements(color)

    // Evaluate positions in parallel, keeping original index for deterministic tie-breaking
    val zippedPlacements = availablePlacements.zipWithIndex
    val evaluatedMoves = zippedPlacements.par.map((position, index) =>
      val newBoard = board.placeDisk(color, position, validatePosition = false)
      val score = negamax(newBoard, depth - 1, color.opposite)
      (position, score, index)
    )

    val bestMove: Position = evaluatedMoves.seq.minBy((_, score, index) => (score, index))._1
    bestMove
