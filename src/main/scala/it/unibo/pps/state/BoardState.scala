package it.unibo.pps.state

import it.unibo.pps.utils.Shape

case class BoardState(shape: Shape, disks: Seq[DiskState])
