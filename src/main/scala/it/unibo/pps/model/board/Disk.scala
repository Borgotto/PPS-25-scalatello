package it.unibo.pps.model.board

import it.unibo.pps.domain.Color

/** Represents a generic disk.
 *  @param color the [[domain.Color]] of the disk to create.
 */
case class Disk(color: Color):
  /** @return a disk with the other [[domain.Color]] value. */
  def flip: Disk = Disk(color.opposite)
