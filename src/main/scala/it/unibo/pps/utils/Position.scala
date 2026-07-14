package it.unibo.pps.utils

import upickle.default.ReadWriter

case class Position(row: Int, column: Int) derives ReadWriter:
  def -(pos: Position): Position =
    Position(this.row - pos.row, this.column - pos.column)
