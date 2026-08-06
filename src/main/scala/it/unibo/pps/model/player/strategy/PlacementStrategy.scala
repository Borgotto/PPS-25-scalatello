package it.unibo.pps.model.player.strategy

import it.unibo.pps.domain.{Color, Position}
import it.unibo.pps.model.board.Board

import scala.util.Random
import upickle.default.ReadWriter

/**
 * Generic strategy for computing a placement given some context.
 * 
 * Concrete strategies implement the computation of a placement using
 * the given context via Scala 3 `using` (contextual) parameter.
 *
 * @tparam C the type of the context required to compute a placement
 *           (e.g. [[domain.Position]] for user choice, or [[model.board.Board]] for an opponent
 *            whose decision depends on the current board state).
 * @tparam O the output of the computed placement or decision
 * @note This trait is contravariant in `C` (the context type), this allows a strategy that requires
 * a more general context to be used in place of one that requires a more specific context.
 */
trait PlacementStrategy[-C, O]:
  /**
   * Compute a placement using the provided contextual parameter.
   *
   * This design lets callers supply the context implicitly when invoking strategies.
   *
   * @param context the information used to compute the placement
   * @return an output representing the chosen placement/decision
   */
  def computePlacement(using context: C): O

/**
 * Trait for [[Opponent]]s' placement strategies.
 *
 * Opponent strategies compute a placement based on the current [[model.board.Board]] and produce a [[domain.Position]].
 * 
 * @note This trait is sealed to be able to derive a [[upickle.default.ReadWriter]] for
 * serialization support.
 */
sealed trait OpponentPlacementStrategy extends PlacementStrategy[Board, Position] derives ReadWriter

/**
 * Erratic opponent placement strategy.
 *
 * Chooses one of the available placements on the given board at random.
 *
 * @param color the color of the opponent for which placements are requested, used to filter valid placements
 *
 * Behavior:
 * - Queries [[model.board.Board.getAvailablePlacements]] to obtain the set of legal placements for the opponent.
 * - If no placements are available, an IllegalStateException is thrown to signal an unexpected game state.
 * - Otherwise selects one element at random and returns it.
 *
 * @throws java.lang.IllegalStateException when called while the opponent has no available placements on the board.
 */
case class ErraticPlacementStrategy(color: Color) extends OpponentPlacementStrategy:
  def computePlacement(using board: Board): Position =
    val availablePlacements = board.getAvailablePlacements(color)
    if availablePlacements.isEmpty then throw IllegalStateException("Opponent has no available placements")
    val randomIndex = Random.nextInt(availablePlacements.size)
    availablePlacements.toSeq(randomIndex)

/**
 * A "smart" opponent placement strategy that uses a search algorithm to choose an optimal placement.
 *
 * @param color the opponent color for which to compute the best placement
 * @param depth a search depth limit for the algorithm; higher values yield
 *              more optimal placements at the cost of increased computation time
 *
 * @note This strategy delegates to [[StrategyComputations.calculateBestPlacement]] to perform the actual
 * evaluation/search. The method uses the provided `board` and the configured `depth`
 * to return the best [[domain.Position]] found by the algorithm.
 */
case class SmartPlacementStrategy(color: Color, depth: Int) extends OpponentPlacementStrategy:
  def computePlacement(using board: Board): Position =
    StrategyComputations.calculateBestPlacement(color, depth)
