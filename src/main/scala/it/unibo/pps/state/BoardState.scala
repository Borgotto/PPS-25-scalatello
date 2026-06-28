package it.unibo.pps.state

import it.unibo.pps.utils.Shape

class BoardState(
  val shape: Shape,
  val disks: Seq[DiskState]
)
