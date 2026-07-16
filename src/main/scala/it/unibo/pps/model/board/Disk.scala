package it.unibo.pps.model.board

import it.unibo.pps.utils.Color

/** Represents a generic disk.
 * 
 * It has a method to [[flip()]] the disk.
 * 
 * @param color the color of the disk to create.
 */
case class Disk(color: Color):
  /** @return a disk with the opposite color of the initial one. */
  def flip: Disk = Disk(color.opposite)
