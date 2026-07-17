package it.unibo.pps.model.board

import it.unibo.pps.utils.{Position, Shape}

/** Helper class that contains extension methods of [[Int]] and [[Position]] to make computations on a
 * [[Shape.Rectangle]] [[Board]].
 *
 * Extends: [[PosComputeExtensions]]
 */
private[board] class PosComputeExtensionsRectangle extends PosComputeExtensions:
  extension (p: Position)
    /** @inheritdoc
     * Overrides: [[PosComputeExtensions.inBounds()]].
     * @param shape the [[Shape]] of the [[Board]].
     * @return `true` if the [[Position]] is in the bounds of the [[Board]], `false` otherwise.
     */
    override def inBounds(shape: Shape): Boolean = super.inBounds(p)(shape)
