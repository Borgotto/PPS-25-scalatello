package it.unibo.pps.model.board

import it.unibo.pps.utils.Color

case class Disk(color: Color):
  def flip(): Disk = Disk(color.opposite)
