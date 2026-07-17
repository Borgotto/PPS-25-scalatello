package it.unibo.pps.model.board

import it.unibo.pps.utils.IntExtensions.inRange
import it.unibo.pps.utils.{Position, Shape}

/** Helper class that contains extension methods of [[Int]] and [[Position]] to make computations on a
 * [[Shape.Square]] and [[Shape.Rectangle]] [[Board]].
 *
 * Extends: [[PosComputeExtensions]].
 */
private[board] class PosComputeExtensionsRectangle extends PosComputeExtensions:
  extension (p: Position)
    /** @inheritdoc
     * Overrides: [[PosComputeExtensions.inBounds()]].
     * @param shape the [[Shape]] of the [[Board]].
     * @return `true` if the [[Position]] is in the bounds of the [[Board]], `false` otherwise.
     */
    def inBounds(shape: Shape): Boolean =
      val minPosition: Position = Position(0, 0)
      p.row.inRange(minPosition.row, shape.maxRow) && p.column.inRange(minPosition.column, shape.maxColumn)
