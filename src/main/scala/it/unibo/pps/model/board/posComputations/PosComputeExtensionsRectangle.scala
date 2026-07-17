package it.unibo.pps.model.board.posComputations

import it.unibo.pps.model.board.posComputations.PosComputeExtensions
import it.unibo.pps.utils.IntExtensions.inRange
import it.unibo.pps.utils.{Position, Shape}

/** Helper class that contains extension methods of [[Position]] to make computations on a
 * [[Shape.Square]] and [[Shape.Rectangle]] [[Board]].
 *
 * Extends the trait: [[PosComputeExtensions]].
 */
private[board] class PosComputeExtensionsRectangle extends PosComputeExtensions:
  extension (p: Position)
    /** @inheritdoc
     * Implements: [[PosComputeExtensions.inBounds()]].
     * @param shape the [[Shape]] of the board.
     * @return `true` if the position is in the bounds of the board, `false` otherwise.
     */
    def inBounds(shape: Shape): Boolean =
      val minPosition: Position = Position(0, 0)
      p.row.inRange(minPosition.row, shape.maxRow) && p.column.inRange(minPosition.column, shape.maxColumn)
