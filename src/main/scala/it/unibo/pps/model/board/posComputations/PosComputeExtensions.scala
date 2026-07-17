package it.unibo.pps.model.board.posComputations

import it.unibo.pps.utils.IntExtensions.inBetween
import it.unibo.pps.utils.{Position, Shape}

import scala.annotation.tailrec

/** Helper trait that contains extension methods of [[Position]] to make computations on a [[Board]].
 * 
 * Contains methods to know:
 * - if this position is on the same diagonal and in between two positions: [[onSameDiagonal()]];
 * - if this position is in between two positions: [[inBetweenPos()]];
 * - if this position is in the same neighbourhood as another one: [[inNeighbourhood()]];
 * - if this position is in the bounds of a [[Shape]]: [[inBounds()]].
 *
 * Every class that uses it must implement the [[inBounds()]] method.
 *
 * Used by: [[PosComputeExtensionsRectangle]].
 */
private[board] trait PosComputeExtensions:
  extension (p: Position)
    /** Extension method of [[Position]].
     *
     * Used to know if this position is on the same diagonal of `firstPos` and `secondPos` and also between them.
     * @param firstPos the starting position to be considered on the diagonal.
     * @param secondPos the last position to be considered on the diagonal.
     * @return `true` if the position is on the same diagonal and between `firstPos` and `secondPos`, `false` otherwise.
     */
    private def onSameDiagonal(firstPos: Position, secondPos: Position): Boolean =
      /** Helper method to get the [[Position]] of all the [[Disk]] that are on the same diagonal
       * and between `source` and `destination`.
       *
       * @param source      the position of the disk from which the research originates.
       * @param destination the position of the disk where the research stops.
       * @param direction   the direction to follow to go towards the `destination`.
       * @param acc         an accumulator, that at the end will contain all the position that satisfy the conditions.
       * @return the final accumulator containing all the positions on the same diagonal and between `source` and `destination`.
       */
      @tailrec
      def _getPosOnSameDiagonal(source: Position, destination: Position,
                                       direction: Position, acc: Set[Position] = Set()): Set[Position] =
        val nextPos: Position = source - direction
        (nextPos, destination) match
          case (f, s) if f.equals(s) => acc
          case (f, s) => _getPosOnSameDiagonal(f, s, direction, acc + f)

      val distance: Position = firstPos - secondPos
      val direction: Position = distance / Position(distance.row.abs, distance.column.abs)
      _getPosOnSameDiagonal(firstPos, secondPos, direction).contains(p) &&
        distance.row.abs.equals(distance.column.abs)

    /** Extension method of [[Position]].
     *
     * This is used to know if this position is between `firstPos` and `secondPos` vertically, horizontally and diagonally.
     * @param firstPos the starting position to consider.
     * @param secondPos the last position to consider.
     * @return `true` if this position is between `firstPos` and `secondPos`, `false` otherwise.
     */
    def inBetweenPos(firstPos: Position, secondPos: Position): Boolean =
      (firstPos, secondPos) match
        case (f, s) if f.row.equals(s.row) => p.column.inBetween(f.column, s.column) && p.row.equals(f.row)
        case (f, s) if f.column.equals(s.column) => p.row.inBetween(f.row, s.row) && p.column.equals(f.column)
        case (f, s) => p.onSameDiagonal(f, s)

    /** Extension method of [[Position]].
     *
     * This is used to determine if this position is in the neighbourhood of `pos`.
     *
     * Being in the neighbourhood of a position means that the distance between them is inside the range [-1, 1].
     * @param pos the position of which to consider the neighbourhood of.
     * @return `true` if this position is in the neighbourhood of `pos`, `false` otherwise.
     */
    def inNeighbourhood(pos: Position): Boolean =
      val maxDistance: Int = 1
      val distance: Position = p - pos
      distance.row.abs <= maxDistance && distance.column.abs <= maxDistance

    /** Extension method of [[Position]].
     *
     * Used to know if this position is in the bounds of the [[Board]].
     * @param shape the [[Shape]] of the board.
     * @return `true` if the position is in the bounds of the board, `false` otherwise.
     */
    def inBounds(shape: Shape): Boolean