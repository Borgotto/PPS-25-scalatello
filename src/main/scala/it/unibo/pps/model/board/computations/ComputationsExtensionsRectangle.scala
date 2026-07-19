package it.unibo.pps.model.board.computations

import it.unibo.pps.domain.{Position, Shape}
import it.unibo.pps.utils.IntExtensions.inRange

/** Helper class that contains extension methods of [[Position]] to make computations on a square and rectangular [[Board]]. */
private[board] class ComputationsExtensionsRectangle extends ComputationsExtensions:
  extension (p: Position)
    /** @inheritdoc
     *  @param shape the [[Shape]] of the board.
     *  @return `true` if the position is in the bounds of the board, `false` otherwise.
     */
    def inBounds(shape: Shape): Boolean =
      val minPosition: Position = Position(0, 0)
      p.row.inRange(minPosition.row, shape.maxRow) && p.column.inRange(minPosition.column, shape.maxColumn)
