package it.unibo.pps.model.board

import it.unibo.pps.utils.Color

private[board] case class Disk(color: Color):
  def flip(): Disk = Disk(color.opposite)
