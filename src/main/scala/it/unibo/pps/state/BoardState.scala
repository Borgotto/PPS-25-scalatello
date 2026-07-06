package it.unibo.pps.state

import it.unibo.pps.utils.{Position, Shape}

case class BoardState(
  shape: Shape,
  disks: Seq[DiskState],
  userAvailablePlacements: Set[Position]
)
