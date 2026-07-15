package it.unibo.pps.model.board

import it.unibo.pps.utils.{Position, Shape}

private[board] class PosComputeExtensionsRectangle extends PosComputeExtensions:
  extension (p: Position) 
    override def inBounds(shape: Shape): Boolean = super.inBounds(p)(shape)
