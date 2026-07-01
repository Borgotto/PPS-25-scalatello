package it.unibo.pps.model.board

import it.unibo.pps.state.DiskState
import it.unibo.pps.utils.{Color, Position}

class Disk(color: Color, position: Position):
  def state = DiskState(color, position)
