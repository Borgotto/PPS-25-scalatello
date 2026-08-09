package it.unibo.pps.model.board.computations

import it.unibo.pps.domain.{Position, Shape}

/** Helper trait that contains extension methods of [[domain.Position]] to make computations on a [[model.board.Board]].
 *
 *  All methods must be specified by every class using it.
 */
private[board] trait ComputationsPosExtensions:
  extension (p: Position)
    /** Used to know if this position is in the bounds of the [[model.board.Board]].
     * 
     *  This is an extension method of [[domain.Position]].
     *  @param shape the [[domain.Shape]] of the board.
     *  @return `true` if the position is in the bounds of the board, `false` otherwise.
     */
    def inBounds(shape: Shape): Boolean
