package it.unibo.pps.model.board.computations

import it.unibo.pps.domain.{Position, Shape}
import it.unibo.pps.utils.IntExtensions.inBetween

import scala.annotation.tailrec

/** Helper trait that contains extension methods of [[Position]] to make computations on a [[Board]].
 *
 *  Every class that uses it must specify the [[inBounds()]] method.
 *
 *  Used by: [[ComputationsPosExtensionsRectangle]].
 */
private[board] trait ComputationsPosExtensions:
  extension (p: Position)
    /** Used to know if this position is on the same diagonal of `firstPos` and `secondPos` and also between them.
     * 
     *  This is an extension method of [[Position]].
     *  @param firstPos the starting position to be considered on the diagonal.
     *  @param secondPos the last position to be considered on the diagonal.
     *  @return `true` if the position is on the same diagonal and between `firstPos` and `secondPos`, `false` otherwise.
     */
    def onSameDiagonal(firstPos: Position, secondPos: Position): Boolean =
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

    /** This is used to know if this position is between `firstPos` and `secondPos` vertically, horizontally and diagonally.
     * 
     *  This is an extension method of [[Position]].
     *  @param firstPos the starting position to consider.
     *  @param secondPos the last position to consider.
     *  @return `true` if this position is between `firstPos` and `secondPos`, `false` otherwise.
     */
    def inBetweenPos(firstPos: Position, secondPos: Position): Boolean =
      (firstPos, secondPos) match
        case (f, s) if f.row.equals(s.row) => p.column.inBetween(f.column, s.column) && p.row.equals(f.row)
        case (f, s) if f.column.equals(s.column) => p.row.inBetween(f.row, s.row) && p.column.equals(f.column)
        case (f, s) => p.onSameDiagonal(f, s)

    /** This is used to determine if this position is in the neighbourhood of `pos`.
     *
     *  Being in the neighbourhood of a position means that the distance between them is inside the range [-1, 1].
     * 
     *  This is an extension method of [[Position]].
     *  @param pos the position of which to consider the neighbourhood of.
     *  @return `true` if this position is in the neighbourhood of `pos`, `false` otherwise.
     */
    def inNeighbourhood(pos: Position): Boolean =
      val maxDistance: Int = 1
      val distance: Position = p - pos
      distance.row.abs <= maxDistance && distance.column.abs <= maxDistance

    /** Used to know if this position is in the bounds of the [[Board]].
     * 
     *  This is an extension method of [[Position]].
     *  @param shape the [[Shape]] of the board.
     *  @return `true` if the position is in the bounds of the board, `false` otherwise.
     */
    def inBounds(shape: Shape): Boolean