package it.unibo.pps.model.board

import it.unibo.pps.utils.{Position, Shape}
import it.unibo.pps.utils.IntExtensions.inBetween

import scala.annotation.tailrec
import scala.math.Ordering.Int

/** Helper trait that contains extension methods of [[Position]] to make computations on a [[Board]].
 *
 * Every class that uses it must implement the [[inBounds()]] method.
 *
 * Used by: [[PosComputeExtensionsRectangle]].
 */
private[board] trait PosComputeExtensions:
  extension (p: Position)
    /** Extension method of [[Position]].
     *
     * This divides two [[Position]] instances.
     *
     * This ignores the division with 0, if a number is divided by 0 the result is 0.
     * @param pos the [[Position]] to divide this [[Position]] with.
     * @return the result of the division.
     */
    def /(pos: Position): Position =
      (pos.row, pos.column) match
        case (r, c) if r.equals(0) && c.equals(0) => Position(r, c)
        case (r, c) if r.equals(0) => Position(r, p.column / c)
        case (r, c) if c.equals(0) => Position(p.row / r, c)
        case (_, _) => Position(p.row / pos.row, p.column / pos.column)

    /** Extension method of [[Position]].
     *
     * Used to know if this [[Position]] is on the same diagonal of `firstPos` and `secondPos` and also between them.
     * @param firstPos the starting [[Position]] to be considered on the diagonal.
     * @param secondPos the last [[Position]] to be considered on the diagonal.
     * @return `true` if the [[Position]] is on the same diagonal and between `firstPos` and `secondPos`, `false` otherwise.
     */
    private def onSameDiagonal(firstPos: Position, secondPos: Position): Boolean =
      /** Helper method to get the [[Position]] of all the [[Disk]] that are on the same diagonal
       * and between `source` and `destination`.
       *
       * @param source      the [[Position]] of the [[Disk]] from which the research originates.
       * @param destination the [[Position]] of the [[Disk]] where the research stops.
       * @param direction   the direction to follow to go towards the `destination`.
       * @param acc         an accumulator, that at the end will contain all the [[Position]] that satisfy the conditions.
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
     * This is used to know if this [[Position]] is between `firstPos` and `secondPos` vertically, horizontally and diagonally.
     * @param firstPos the starting [[Position]] to consider.
     * @param secondPos the last [[Position]] to consider.
     * @return `true` if this [[Position]] is between `firstPos` and `secondPos`, `false` otherwise.
     */
    def inBetweenPos(firstPos: Position, secondPos: Position): Boolean =
      (firstPos, secondPos) match
        case (f, s) if f.row.equals(s.row) => p.column.inBetween(f.column, s.column) && p.row.equals(f.row)
        case (f, s) if f.column.equals(s.column) => p.row.inBetween(f.row, s.row) && p.column.equals(f.column)
        case (f, s) => p.onSameDiagonal(f, s)

    /** Extension method of [[Position]].
     *
     * This is used to determine if this [[Position]] is in the neighbourhood of `pos`.
     *
     * Being in the neighbourhood of a [[Position]] means that the distance between them is inside the range [-1, 1].
     * @param pos the [[Position]] of which to consider the neighbourhood of.
     * @return `true` if this [[Position]] is in the neighbourhood of `pos`, `false` otherwise.
     */
    def inNeighbourhood(pos: Position): Boolean =
      val maxDistance: Int = 1
      val distance: Position = p - pos
      distance.row.abs <= maxDistance && distance.column.abs <= maxDistance

    /** Extension method of [[Position]].
     *
     * Used to know if this [[Position]] is in the bounds of the [[Board]].
     * @param shape the [[Shape]] of the [[Board]].
     * @return `true` if the [[Position]] is in the bounds of the [[Board]], `false` otherwise.
     */
    def inBounds(shape: Shape): Boolean