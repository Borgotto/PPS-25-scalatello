package it.unibo.pps.model.board

import it.unibo.pps.domain.Color

/** Represents a generic disk.
 *  @param color the color of the disk to create.
 */
case class Disk(color: Color):
  /** @return a disk with the other [[Color]] value. */
  def flip: Disk = Disk(color.opposite)
