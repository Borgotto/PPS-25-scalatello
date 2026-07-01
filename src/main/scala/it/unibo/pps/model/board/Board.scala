package it.unibo.pps.model.board

import it.unibo.pps.utils.Color
import it.unibo.pps.utils.Position

abstract case class Board():
  def getBoard: Board
  def getAvailableMoves(color: Color): List[Position]