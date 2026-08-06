package it.unibo.pps.model.board.computations

import it.unibo.pps.domain.{Position, Shape}
import it.unibo.pps.utils.IntExtensions.inRange

/** Helper class that contains extension methods of [[domain.Position]] 
 *  to make computations on a square and rectangular [[model.board.Board]]. 
 */
private[board] class ComputationsPosExtensionsRectangle extends ComputationsPosExtensions:
  extension (p: Position)
    def inBounds(shape: Shape): Boolean =
      val minPosition: Position = Position(0, 0)
      p.row.inRange(minPosition.row, shape.maxRowIndex) && p.column.inRange(minPosition.column, shape.maxColumnIndex)
